//
//  AnalyzingViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import CoreMedia
import Vision
import FirebaseStorage
import FirebaseFirestore
import Firebase

class AnalyzingViewController: UIViewController
{
    var pickedImageData:UIImage!;
    var onetime:Bool?;
    let userDefaults = UserDefaults.standard
    @IBOutlet weak var pickedImage: UIImageView!
    let inceptionv3model = classes()
    var results:[String] = [];
    var arrayOfCodes:[String] = []
    var arrayOfPhotos:[String] = []
    var images:[UIImage] = []
    var imagesBW:[UIImage] = []
    var allresults:NSMutableArray?
    var results1:NSMutableArray?
    var results2:NSMutableArray?
    var csvInts:[[Double]] = []
    var indicies:[String] = []
    
    func randomString(length: Int) -> String {
        
        let letters : NSString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        let len = UInt32(letters.length)
        
        var randomString = ""
        
        for _ in 0 ..< length {
            let rand = arc4random_uniform(len)
            var nextChar = letters.character(at: Int(rand))
            randomString += NSString(characters: &nextChar, length: 1) as String
        }
        
        return randomString
    }
    
    func pixelValues(fromCGImage imageRef: CGImage?) -> (pixelValues: [UInt8]?, width: Int, height: Int)
    {
        var width = 0
        var height = 0
        var pixelValues: [UInt8]?
        if let imageRef = imageRef {
            width = imageRef.width
            height = imageRef.height
            let bitsPerComponent = imageRef.bitsPerComponent
            let bytesPerRow = imageRef.bytesPerRow
            let totalBytes = height * bytesPerRow
            
            let colorSpace = CGColorSpaceCreateDeviceGray()
            var intensities = [UInt8](repeating: 0, count: totalBytes)
            
            let contextRef = CGContext(data: &intensities, width: width, height: height, bitsPerComponent: bitsPerComponent, bytesPerRow: bytesPerRow, space: colorSpace, bitmapInfo: 0)
            contextRef?.draw(imageRef, in: CGRect(x: 0.0, y: 0.0, width: CGFloat(width), height: CGFloat(height)))
            
            pixelValues = intensities
        }
        
        return (pixelValues, width, height)
    }
    
    func image(fromPixelValues pixelValues: [UInt8]?, width: Int, height: Int) -> CGImage?
    {
        var imageRef: CGImage?
        if var pixelValues = pixelValues {
            let bitsPerComponent = 8
            let bytesPerPixel = 1
            let bitsPerPixel = bytesPerPixel * bitsPerComponent
            let bytesPerRow = bytesPerPixel * width
            let totalBytes = height * bytesPerRow
            
            imageRef = withUnsafePointer(to: &pixelValues, {
                ptr -> CGImage? in
                var imageRef: CGImage?
                let colorSpaceRef = CGColorSpaceCreateDeviceGray()
                let bitmapInfo = CGBitmapInfo(rawValue: CGImageAlphaInfo.none.rawValue).union(CGBitmapInfo())
                let data = UnsafeRawPointer(ptr.pointee).assumingMemoryBound(to: UInt8.self)
                let releaseData: CGDataProviderReleaseDataCallback = {
                    (info: UnsafeMutableRawPointer?, data: UnsafeRawPointer, size: Int) -> () in
                }
                
                if let providerRef = CGDataProvider(dataInfo: nil, data: data, size: totalBytes, releaseData: releaseData) {
                    imageRef = CGImage(width: width,
                                       height: height,
                                       bitsPerComponent: bitsPerComponent,
                                       bitsPerPixel: bitsPerPixel,
                                       bytesPerRow: bytesPerRow,
                                       space: colorSpaceRef,
                                       bitmapInfo: bitmapInfo,
                                       provider: providerRef,
                                       decode: nil,
                                       shouldInterpolate: false,
                                       intent: CGColorRenderingIntent.defaultIntent)
                }
                
                return imageRef
            })
        }
        
        return imageRef
    }

    func uploadResults() {
        
        let db = Firestore.firestore();
        var ref: DocumentReference? = nil
        var code = randomString(length: 5)
        
        ref = db.collection("SocialContent").addDocument(data: [
            "Description": "",
            "GardinerCode": code,
            "Owner": userDefaults.string(forKey: "Name")!,
            "Photo": "/UserPictures/"+userDefaults.string(forKey: "Name")!+code
        ]) { err in
            if let err = err {
                print("Error adding document: \(err)")
            } else {
                print("Document added with ID: \(ref!.documentID)")
            }
        }
        
        let imageData:NSData = UIImagePNGRepresentation(self.pickedImageData)! as NSData
        var strBase64 = imageData.base64EncodedString(options: .lineLength64Characters)
        
        strBase64 = strBase64.replacingOccurrences(of: " ", with: "")
        strBase64 = strBase64.replacingOccurrences(of: "\n", with: "")
        
        let storage = Storage.storage()
        let storageRef = storage.reference()
        let userRef = storageRef.child("/UserPictures/"+userDefaults.string(forKey: "Name")!+code+".bin")
        
        let uploadTask = userRef.putData((imageData as Data?)!, metadata: nil) { (metadata, error) in
            guard let metadata = metadata else {
                // Uh-oh, an error occurred!
                return
            }

        self.arrayOfCodes.append(self.results[self.results.count-1])
        self.arrayOfPhotos.append(strBase64)
        self.performSegue(withIdentifier: "results", sender: nil)
        
    }
    }
    
    func predict(index:Int) {
        if let path = Bundle.main.path(forResource: "ios", ofType:"txt") {
            // use path

            do {
        
                
                let prediction = try self.inceptionv3model.prediction(inputnames: resize(pixelBuffer: buffer(from: images[index])!)!)

                var sum:Double = 0
                var dist:Double = 0
                var min:Double = 10000
                var indexx:Int = 0
                 let classes = try String(contentsOfFile: path)
                 let Classes = classes.components(separatedBy: "\n")
                var topp:String = ""
                
                var distance:Dictionary = [Double: String]()
                
                for i in 0..<157 {
                    for j in 0..<640 {
                        dist = abs(Double( truncating: prediction.output1[j]) - csvInts[i][j])
                        sum = sum + dist
                        dist = 0
                    }

                    distance[sum] = Classes[i]
                    sum = 0
              
                }

                let top5 = top(5, distance)
               topp = ""
                
                let dists = distance.keys.sorted(by: <)
            
                for x in 0..<5 {
                    
                    topp = topp + distance[dists[x]]!
                    if(x != 4) {
                        topp = topp + ","
                    }
                }
                
                self.results.append(topp)
                
            }
            catch let error as NSError {
                fatalError("Unexpected error ocurred: \(error.localizedDescription).")
            }
        }
    }
    
    func csv(data: String) -> [[String]] {
        var result: [[String]] = []
        let rows = data.components(separatedBy: "\n")
        for row in rows {
            let columns = row.components(separatedBy: ",")
            result.append(columns)
        }
        return result
    }
    
    func readDataFromCSV(fileName:String, fileType: String)-> String!{
        guard let filepath = Bundle.main.path(forResource: fileName, ofType: fileType)
            else {
                return nil
        }
        do {
            var contents = try String(contentsOfFile: filepath, encoding: .utf8)
            contents = cleanRows(file: contents)
            return contents
        } catch {
            print("File Read Error for file \(filepath)")
            return nil
        }
    }
    
    func cleanRows(file:String)->String{
        var cleanFile = file
        cleanFile = cleanFile.replacingOccurrences(of: "\r", with: "\n")
        cleanFile = cleanFile.replacingOccurrences(of: "\n\n", with: "\n")
        return cleanFile
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        pickedImage.image = pickedImageData;
        
        var data = readDataFromCSV(fileName: "vectors", fileType: ".csv")
        data = cleanRows(file: data!)
        var csvRows = csv(data: data!)
    
        csvInts = Array(repeating: Array(repeating: 0.0 , count: 640), count: 157)
      
        for var i in 0..<157  {
            for var j in 0..<640 {
                csvRows[i][j] = csvRows[i][j].replacingOccurrences(of: "\"", with: "")
                
                csvInts[i][j] = Double(csvRows[i][j])!
            }
        }
        //157 row, 640 column
        
    }
    
    
    func buffer(from image: UIImage) -> CVPixelBuffer? {
        
        let attrs = [kCVPixelBufferCGImageCompatibilityKey: kCFBooleanTrue, kCVPixelBufferCGBitmapContextCompatibilityKey: kCFBooleanTrue]
        var maybePixelBuffer : CVPixelBuffer?
        let status = CVPixelBufferCreate(kCFAllocatorDefault, Int(image.size.width), Int(image.size.height), kCVPixelFormatType_OneComponent8, attrs as CFDictionary, &maybePixelBuffer)
        guard status == kCVReturnSuccess, let pixelBuffer = maybePixelBuffer else {
            return nil
        }
        
        CVPixelBufferLockBaseAddress(pixelBuffer, CVPixelBufferLockFlags(rawValue: 0))
        let pixelData = CVPixelBufferGetBaseAddress(pixelBuffer)
        
        let rgbColorSpace = CGColorSpaceCreateDeviceGray()
        guard let context = CGContext(data: pixelData, width: Int(image.size.width), height: Int(image.size.height), bitsPerComponent: 8, bytesPerRow: CVPixelBufferGetBytesPerRow(pixelBuffer), space: rgbColorSpace, bitmapInfo: CGImageAlphaInfo.none.rawValue)
            else {
                return nil
        }
        
        UIGraphicsPushContext(context)
        context.translateBy(x: 0, y: image.size.height)
        context.scaleBy(x: 1.0, y: -1.0)
        image.draw(in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))
        UIGraphicsPopContext()
        CVPixelBufferUnlockBaseAddress(pixelBuffer, CVPixelBufferLockFlags(rawValue: 0))
   
        return pixelBuffer
    }
    
    func resize(pixelBuffer: CVPixelBuffer) -> CVPixelBuffer? {
        let imageSideX = 50
        let imageSideY = 75
        var ciImage = CIImage(cvPixelBuffer: pixelBuffer, options: nil)
        let transform = CGAffineTransform(scaleX: CGFloat(imageSideX) / CGFloat(CVPixelBufferGetWidth(pixelBuffer)), y: CGFloat(imageSideY) / CGFloat(CVPixelBufferGetHeight(pixelBuffer)))
        ciImage = ciImage.transformed(by: transform).cropped(to: CGRect(x: 0, y: 0, width: imageSideX, height: imageSideY))
        let ciContext = CIContext()
        var resizeBuffer: CVPixelBuffer?
        CVPixelBufferCreate(kCFAllocatorDefault, imageSideX, imageSideY, CVPixelBufferGetPixelFormatType(pixelBuffer), nil, &resizeBuffer)
        ciContext.render(ciImage, to: resizeBuffer!)
        return resizeBuffer
    }
    
    public func top(_ k: Int, _ prob: [Double: String]) -> [(String, Double)] {
        return Array(prob.map { x in (x.value, x.key) }
            .sorted(by: { a, b -> Bool in a.1 > b.1 })
            .prefix(through: min(k, prob.count) - 1))
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)

        self.imagesBW.removeAll()
        self.images.removeAll()
        self.results1?.removeAllObjects()
        self.results2?.removeAllObjects()
        self.results.removeAll()
        self.allresults?.removeAllObjects()
        
        self.hideKeyboardWhenTappedAround()
        let openCVWrapper = OpenCVWrapper()

        allresults = openCVWrapper.segment(pickedImageData)
        results1 = allresults![0] as! NSMutableArray
        results2 = allresults![1] as! NSMutableArray
 
        pickedImage.image = results1![(results1?.count)!-1] as? UIImage;
        
        for index in 0...(results2?.count)!-2 {
            images.append(results2![index] as! UIImage)
            predict(index: index)
        }

        for index in 0...(results1?.count)!-2 {
            if (index == 0) {
                self.imagesBW.removeAll()
            }
             imagesBW.append(results1![index] as! UIImage)
        }

        if (!onetime!) {
            uploadResults()
        }
        else {
             self.performSegue(withIdentifier: "results", sender: self)
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
         self.imagesBW.removeAll()
         self.images.removeAll()
         self.arrayOfCodes.removeAll()
         self.arrayOfPhotos.removeAll()
         self.results1?.removeAllObjects()
         self.results2?.removeAllObjects()
         self.results.removeAll()
         self.allresults?.removeAllObjects()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "results") {
            let destVC: HistoryViewController=segue.destination as! HistoryViewController
            destVC.arrayOfPhotos.removeAll()
            destVC.arrayOfCodes.removeAll()
            destVC.arrayOfOwners.removeAll()
            destVC.arrayOfCodes = self.results;
            destVC.arrayOfImages.removeAll()
            destVC.arrayOfImages = self.imagesBW;
            destVC.results = true
            destVC.onetime = self.onetime!
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
