//
//  ResultsViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseFirestore
import FirebaseStorage

class ResultsViewController: UIViewController {
    @IBOutlet weak var loading: UIImageView!
    @IBOutlet weak var dictionayLink: UILabel!
    @IBOutlet weak var usefullinksTitle: UIImageView!
    @IBOutlet weak var usefullinkField: UILabel!
    @IBOutlet weak var webImage: UIImageView!
    @IBOutlet weak var pickedImage: UIImageView!
    @IBOutlet weak var codeField: UILabel!
    @IBOutlet weak var meaningField: UILabel!
    @IBOutlet weak var descriptionField: UILabel!
    var isresults:Bool = false;
    var image:UIImage!
    var onetime:Bool = true;
    @IBOutlet weak var waiting: UIActivityIndicatorView!
    var pickedDataImage:UIImage!
    var results:String = "0";
    var arrayOfCodes:[String] = []
    var arrayOfImages:[UIImage] = []
    var top5:[String] = []
    var index:Int = 0;
    @IBOutlet weak var previousButton: UIButton!
    @IBOutlet weak var nextButton: UIButton!
    
    @IBAction func previousButtonPressed(_ sender: Any) {
        if (index > 0) {
            index=index-1;
            getInfo()
        } else {
            previousButton.isEnabled = false
        }
    }
    
    @IBAction func nextButtonPressed(_ sender: Any) {
        if (index < 4) {
            index=index+1;
            getInfo()
        } else {
            nextButton.isEnabled = false
        }
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
            performSegue(withIdentifier: "BackR", sender: nil)
    }
    
    @objc func tap1(sender: UITapGestureRecognizer) {
        if(usefullinkField.text != "") {
            UIApplication.shared.open(NSURL(string: usefullinkField.text!)! as URL, options: [:], completionHandler: nil)
        }
    }
    
    @objc func tap2(sender: UITapGestureRecognizer) {
        if(dictionayLink.text != "") {
            UIApplication.shared.open(NSURL(string: dictionayLink.text!)! as URL, options: [:], completionHandler: nil)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if (!isresults) {
            pickedImage.image = pickedDataImage;
        } else {
            pickedImage.image = image;
        }
        self.usefullinksTitle.isHidden = true;
        self.usefullinkField.isHidden = true;
        self.hideKeyboardWhenTappedAround()
        self.waiting.isHidden = false;
        self.loading.isHidden = false;
        let tapGesture1 = UITapGestureRecognizer(target: self, action: #selector(self.tap1))
        
        let tapGesture2 = UITapGestureRecognizer(target: self, action: #selector(self.tap2))
        
        usefullinkField.addGestureRecognizer(tapGesture1)
        dictionayLink.addGestureRecognizer(tapGesture2)
        // Do any additional setup after loading the view.
    }
    
    func getInfo() {
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
                let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to view extra information.\n\nThe top 5 possibilities for the Gardiner's Code for this hieroglyph are: "+self.results, preferredStyle: .alert)
                
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default) {
                    (_) -> Void in
                    
                    if (self.onetime) {
                        self.performSegue(withIdentifier: "OnetimeR", sender: nil)
                    }
                    else {
                        self.performSegue(withIdentifier: "SuccessR", sender: nil)
                    }
                }
                let settingsAction = UIAlertAction(title: "Settings", style: .default) { (_) -> Void in
                    guard let settingsUrl = URL(string: UIApplicationOpenSettingsURLString) else {
                        return
                    }
                    
                    if UIApplication.shared.canOpenURL(settingsUrl) {
                        UIApplication.shared.open(settingsUrl, completionHandler: { (success) in
                            print("Settings opened: \(success)") // Prints true
                        })
                    }
                }
                alertVC.addAction(settingsAction)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
        }
        
        if (reachability.connection == .cellular) || (reachability.connection == .wifi) {
            
            self.top5 = self.results.components(separatedBy: ",")
            
            let db = Firestore.firestore();
            let docRef = db.collection("GardinerCode").document(self.top5[self.index])

            docRef.getDocument { (document, error) in
                if let document = document, document.exists {
                    let dataDescription = document.data().map(String.init(describing:)) ?? "nil"
                    print("Document data: \(dataDescription)")
                    
                    var code : String!
                    var description : String!
                    var image : String!
                    var link : String!
                    var meaning : String!
                    
                    //getting the json response
                    code = self.top5[self.index]
                    description = (document.data()!["Description"]! as? String)!
                    image = (document.data()!["Image"]! as? String)!
                    meaning = (document.data()!["Meaning"]! as? String)!
                    link = (document.data()!["Link"]! as? String)!
                    //printing the response
                    
                    let storage = Storage.storage()
                    let storageRef = storage.reference()
                    let photoRef = storageRef.child(image)
                    
                    photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                        if error != nil {
                            // Uh-oh, an error occurred!
                        } else {
                            self.webImage.image = UIImage(data: data!)
                            self.codeField.text = code;
                            self.descriptionField.text = description;
                            if (link != "") {
                                self.usefullinkField.text = link;
                                self.usefullinkField.isHidden = false;
                                self.usefullinksTitle.isHidden = false;
                            }
                            self.meaningField.text = meaning;
                            
                            if (code.contains("A")) {
                                if (code.contains("Aa")) {
                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/unclassified/" ;
                                }
                                else {
                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/man-and-his-occupations/";
                                }
                            }
                            else
                                if (code.contains("B")) {
                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/woman-and-her-occupations/";
                                }
                                else
                                    if (code.contains("C")) {
                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/anthropomorphic-deities/";
                                    }
                                    else
                                        if (code.contains("D")) {
                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-the-human-body/";
                                        }
                                        else
                                            if (code.contains("E")) {
                                                self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/mammals/";
                                            }
                                            else
                                                if (code.contains("F")) {
                                                    self.dictionayLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-mammals/";
                                                }
                                                else
                                                    if (code.contains("G")) {
                                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/birds/";
                                                    }
                                                    else
                                                        if (code.contains("H")) {
                                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-birds/";
                                                        }
                                                        else
                                                            if (code.contains("I")) {
                                                                self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/amphibious-animals-reptiles-etc/";
                                                            }
                                                            else
                                                                if (code.contains("K")) {
                                                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/fish-and-parts-of-fish/";
                                                                }
                                                                else
                                                                    if (code.contains("L")) {
                                                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/invertebrates-and-lesser-animals/";
                                                                    }
                                                                    else
                                                                        if (code.contains("M")) {
                                                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/trees-and-plants/";
                                                                        }
                                                                        else
                                                                            if (code.contains("N")) {
                                                                                self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/sky-earth-water/";
                                                                            }
                                                                            else
                                                                                if (code.contains("O")) {
                                                                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/buildings-parts-of-buildings-etc/";
                                                                                }
                                                                                else
                                                                                    if (code.contains("P")) {
                                                                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/ships-and-parts-of-ships/";
                                                                                    }
                                                                                    else
                                                                                        if (code.contains("Q")) {
                                                                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/domestic-and-funerary-furniture/";
                                                                                        }
                                                                                        else
                                                                                            if (code.contains("R")) {
                                                                                                self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/temple-furniture-and-sacred-emblems/";
                                                                                            }
                                                                                            else
                                                                                                if (code.contains("S")) {
                                                                                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/crowns-dress-staves-etc/";
                                                                                                }
                                                                                                else
                                                                                                    if (code.contains("T")) {
                                                                                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/warfare-hunting-butchery/";
                                                                                                    }
                                                                                                    else
                                                                                                        if (code.contains("U")) {
                                                                                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/agriculture-crafts-and-professions/";
                                                                                                        }
                                                                                                        else
                                                                                                            if (code.contains("V")) {
                                                                                                                self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/rope-fiber-baskets-bags-etc/";
                                                                                                            }
                                                                                                            else
                                                                                                                if (code.contains("W")) {
                                                                                                                    self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/vessels-of-stone-and-earthenware/";
                                                                                                                }
                                                                                                                else
                                                                                                                    if (code.contains("X")) {
                                                                                                                        self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/loaves-and-cakes/";
                                                                                                                    }
                                                                                                                    else
                                                                                                                        if (code.contains("Y")) {
                                                                                                                            self.dictionayLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/writings-games-music/";
                                                                                                                        }
                                                                                                                        else
                                                                                                                            if (code.contains("Z")) {
                                                                                                                                self.dictionayLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/strokes/";
                            }
                            self.dictionayLink.isHidden = false;
                            self.meaningField.isHidden = false;
                            self.descriptionField.isHidden = false;
                            self.codeField.isHidden = false;
                            self.webImage.isHidden = false;
                            //   self.resultsTitle.isHidden = false;
                            self.waiting.isHidden = true;
                            self.loading.isHidden = true;
                        }
                    }
                    
                    
                } else {
                    print("Document does not exist")
                    let alertVC = UIAlertController(title: "Incorrect Code", message: "Please check the Gardiner code you have entered!", preferredStyle: .alert)
                    
                    let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                    
                    alertVC.addAction(alertActionCancel)
                    self.present(alertVC, animated: true, completion: nil)
                }
            }
            self.reloadInputViews()
        }
    
    }
        
    override func viewDidAppear(_ animated: Bool) {
        getInfo()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackR") {
            let destVC: HistoryViewController=segue.destination as! HistoryViewController
            destVC.arrayOfCodes = self.arrayOfCodes;
            destVC.arrayOfImages = self.arrayOfImages;
            destVC.results = true
            destVC.onetime = self.onetime
        }
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
