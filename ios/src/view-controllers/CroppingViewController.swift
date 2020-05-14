//

//  CroppingViewController.swift

//  DuaKhety

//

//  Created by Malak Sadek on 3/10/18.

//  Copyright © 2018 Malak Sadek. All rights reserved.

//



import UIKit

import AVFoundation

import Photos



class CroppingViewController: UIViewController,
    CroppableImageViewDelegateProtocol,
    UIImagePickerControllerDelegate,
    UINavigationControllerDelegate,
    UIPopoverControllerDelegate {
    
    var pickedImageData:UIImage!;
    var onetime:Bool?;
    @IBOutlet weak var cropButton: UIButton!
    var submit:Int = 0;

    func haveValidCropRect(_ haveValidCropRect: Bool) {
        cropButton.isEnabled = haveValidCropRect
    }
    
    
    
    @IBAction func cropButtonPressed(_ sender: Any) {
        if cropView.croppedImage() != nil
        {
            let imageData = UIImageJPEGRepresentation(cropView.croppedImage()!, 0.6)
            let compressedJPGImage = UIImage(data: imageData!)
            UIImageWriteToSavedPhotosAlbum(compressedJPGImage!, nil, nil, nil)
            if (submit == 0) {
                self.performSegue(withIdentifier: "Analyzing", sender: self)
            } else {
                self.performSegue(withIdentifier: "submit", sender: self)
            }
        }
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        if (onetime!) {
            self.performSegue(withIdentifier: "onemenu", sender: self)
        } else {
             self.performSegue(withIdentifier: "regmenu", sender: self)
        }
    }
    
    
    @IBOutlet weak var cropView:CroppableImageView!
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        perform(#selector(callback), with: nil, afterDelay: 0.2)
    }
    
    @objc func callback() {
       self.cropView.imageToCrop = self.pickedImageData
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "Analyzing") {
        let destVC: AnalyzingViewController=segue.destination as! AnalyzingViewController
        destVC.pickedImageData = cropView.croppedImage()
        destVC.onetime = onetime
        }
        if(segue.identifier == "submit") {
            let destVC: SubmitActivityViewController=segue.destination as! SubmitActivityViewController
                destVC.image = cropView.croppedImage()
                destVC.cropped = 1;
        }
    }
}

