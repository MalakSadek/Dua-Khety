//
//  SearchViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseStorage
import FirebaseFirestore

struct cell {
    var description: String?
    var meaning: String?
    var code: String?
    var photo: Data?
    
    init(description:String? = "", meaning:String?="", code:String?="", photo:Data?=nil) {
        self.description = description
        self.meaning = meaning
        self.code = code
        self.photo = photo
    }
};

class SearchViewController: UIViewController {
    @IBOutlet weak var loading: UIImageView!
    @IBOutlet weak var dictionaryLink: UILabel!
    @IBOutlet weak var usefullinksField: UILabel!
    @IBOutlet weak var usefullinksTitle: UIImageView!
    @IBOutlet weak var meaningField: UILabel!
    @IBOutlet weak var meaningTitle: UIImageView!
    @IBOutlet weak var descriptionField: UILabel!
    @IBOutlet weak var descriptionTitle: UIImageView!
    @IBOutlet weak var codeField: UILabel!
    @IBOutlet weak var codeTitle: UIImageView!
    @IBOutlet weak var webImage: UIImageView!
    //@IBOutlet weak var resultsTitle: UILabel!
    @IBOutlet weak var codeInput: UITextField!
    @IBOutlet weak var waiting: UIActivityIndicatorView!
    @IBOutlet weak var bigusefullinkstitle: UIImageView!
    @IBOutlet weak var infoaboutsymbol: UIImageView!
    var onetime:Bool = true;
    @IBOutlet weak var counterLabel: UILabel!
    
    var arrayOfCells:[cell] = []
    
    @IBAction func backButtonPressed(_ sender: Any) {
        if (onetime) {
            performSegue(withIdentifier: "Onetime", sender: nil)
        }
        else {
             performSegue(withIdentifier: "Success", sender: nil)
        }
    }
    
    @IBAction func viewAllButtonPressed(_ sender: Any) {
        var count = 0;
        let db = Firestore.firestore();
        self.waiting.isHidden = false;
        self.loading.isHidden = false;
        self.counterLabel.text = "0 out of 740 loaded..."
        self.counterLabel.isHidden = false
        let alertVC = UIAlertController(title: "Give us a second!", message: "We're loading 700+ hieroglyphiccs for you, this might take a few minutes, please be patient.", preferredStyle: .alert)
        
        let alertActionCancel = UIAlertAction(title: "Take your time", style: .default, handler: nil)
        alertVC.addAction(alertActionCancel)
        self.present(alertVC, animated: true, completion: nil)
        
        db.collection("GardinerCode").getDocuments() { (querySnapshot, err) in
            if let err = err {
                print("Error getting documents: \(err)")
            } else {
                for document in querySnapshot!.documents {
                    
                    let storage = Storage.storage()
                    let storageRef = storage.reference()
                    let photoRef = storageRef.child((document.data()["Image"]! as? String)!)
                    
                    photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                        if error != nil {
                            // Uh-oh, an error occurred!
       
                        } else {
                            // Data for "images/island.jpg" is returned
                            var mycell = cell(description: (document.data()["Description"]! as? String)!,meaning: (document.data()["Meaning"]! as? String)!,code: document.documentID,photo: data!)
                     
                            self.arrayOfCells.append(mycell)
                            
                            self.counterLabel.text = String(count)+" out of 740 loaded..."
                            
                            if (count == 740){
                                self.arrayOfCells.sort { (lhs: cell, rhs: cell) -> Bool in
                                    // you can have additional code here
                                    return lhs.code! < rhs.code!
                                }
                
                                self.performSegue(withIdentifier: "viewall", sender: nil)
                            }
                            count = count+1
                        }
                    }
                }
            }
        }
    }
    
    @IBAction func searchButtonPressed(_ sender: Any) {
        self.waiting.isHidden = false;
        self.loading.isHidden = false;
        self.dictionaryLink.isHidden = true;
        self.usefullinksTitle.isHidden = true;
        self.usefullinksField.isHidden = true;
        self.meaningField.isHidden = true;
        self.meaningTitle.isHidden = true;
        self.descriptionField.isHidden = true;
        self.descriptionTitle.isHidden = true;
        self.codeField.isHidden = true;
        self.codeTitle.isHidden = true;
        self.webImage.isHidden = true;
        self.bigusefullinkstitle.isHidden = true;
        self.infoaboutsymbol.isHidden = true;
     //   self.resultsTitle.isHidden = true;
       
        if (!codeInput.hasText) {
            let alertVC = UIAlertController(title: "Input Error", message: "You must enter a Gardiner's Code to search!", preferredStyle: .alert)
            self.waiting.isHidden = true;
            self.loading.isHidden = true;
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
            alertVC.addAction(alertActionCancel)
            self.present(alertVC, animated: true, completion: nil)
        }
        else {
            
            let db = Firestore.firestore();
            let docRef = db.collection("GardinerCode").document(self.codeInput.text!)
            
            docRef.getDocument { (document, error) in
                if let document = document, document.exists {
                    let dataDescription = document.data().map(String.init(describing:)) ?? "nil"
                    print("Document data: \(dataDescription)")
                    
                    var description : String!
                    var image : String!
                    var link : String!
                    var meaning : String!
                    var code : String!
                    //getting the json response
                    code = self.codeInput.text
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
                                self.usefullinksField.text = link;
                                self.usefullinksField.isHidden = false;
                                self.usefullinksTitle.isHidden = false;
                            }
                            self.meaningField.text = meaning;
                            
                            if (code.contains("A")) {
                                if (code.contains("Aa")) {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/unclassified/" ;
                                }
                                else {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/man-and-his-occupations/";
                                }
                            }
                            else
                                if (code.contains("B")) {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/woman-and-her-occupations/";
                                }
                                else
                                    if (code.contains("C")) {
                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/anthropomorphic-deities/";
                                    }
                                    else
                                        if (code.contains("D")) {
                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-the-human-body/";
                                        }
                                        else
                                            if (code.contains("E")) {
                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/mammals/";
                                            }
                                            else
                                                if (code.contains("F")) {
                                                    self.dictionaryLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-mammals/";
                                                }
                                                else
                                                    if (code.contains("G")) {
                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/birds/";
                                                    }
                                                    else
                                                        if (code.contains("H")) {
                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-birds/";
                                                        }
                                                        else
                                                            if (code.contains("I")) {
                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/amphibious-animals-reptiles-etc/";
                                                            }
                                                            else
                                                                if (code.contains("K")) {
                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/fish-and-parts-of-fish/";
                                                                }
                                                                else
                                                                    if (code.contains("L")) {
                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/invertebrates-and-lesser-animals/";
                                                                    }
                                                                    else
                                                                        if (code.contains("M")) {
                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/trees-and-plants/";
                                                                        }
                                                                        else
                                                                            if (code.contains("N")) {
                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/sky-earth-water/";
                                                                            }
                                                                            else
                                                                                if (code.contains("O")) {
                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/buildings-parts-of-buildings-etc/";
                                                                                }
                                                                                else
                                                                                    if (code.contains("P")) {
                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/ships-and-parts-of-ships/";
                                                                                    }
                                                                                    else
                                                                                        if (code.contains("Q")) {
                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/domestic-and-funerary-furniture/";
                                                                                        }
                                                                                        else
                                                                                            if (code.contains("R")) {
                                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/temple-furniture-and-sacred-emblems/";
                                                                                            }
                                                                                            else
                                                                                                if (code.contains("S")) {
                                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/crowns-dress-staves-etc/";
                                                                                                }
                                                                                                else
                                                                                                    if (code.contains("T")) {
                                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/warfare-hunting-butchery/";
                                                                                                    }
                                                                                                    else
                                                                                                        if (code.contains("U")) {
                                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/agriculture-crafts-and-professions/";
                                                                                                        }
                                                                                                        else
                                                                                                            if (code.contains("V")) {
                                                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/rope-fiber-baskets-bags-etc/";
                                                                                                            }
                                                                                                            else
                                                                                                                if (code.contains("W")) {
                                                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/vessels-of-stone-and-earthenware/";
                                                                                                                }
                                                                                                                else
                                                                                                                    if (code.contains("X")) {
                                                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/loaves-and-cakes/";
                                                                                                                    }
                                                                                                                    else
                                                                                                                        if (code.contains("Y")) {
                                                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/writings-games-music/";
                                                                                                                        }
                                                                                                                        else
                                                                                                                            if (code.contains("Z")) {
                                                                                                                                self.dictionaryLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/strokes/";
                            }
                            self.dictionaryLink.isHidden = false;
                            self.meaningField.isHidden = false;
                            self.meaningTitle.isHidden = false;
                            self.descriptionField.isHidden = false;
                            self.descriptionTitle.isHidden = false;
                            self.bigusefullinkstitle.isHidden = false;
                            self.infoaboutsymbol.isHidden = false;
                            self.codeField.isHidden = false;
                            self.codeTitle.isHidden = false;
                            self.webImage.isHidden = false;
                            //   self.resultsTitle.isHidden = false;
                            self.waiting.isHidden = true;
                            self.loading.isHidden = true;
                        }
                    }
                    
                    
                } else {
                    print("Document does not exist")
                    let alertVC = UIAlertController(title: "Incorrect Code", message: "Please check the Gardiner code you have entered!", preferredStyle: .alert)
                    self.waiting.isHidden = true;
                    self.loading.isHidden = true;
                    let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                    
                    alertVC.addAction(alertActionCancel)
                    self.present(alertVC, animated: true, completion: nil)
                }
            }
            self.reloadInputViews()
        }
    }
    
    
    @objc func tap1(sender: UITapGestureRecognizer) {
        if(usefullinksField.text != "") {
            UIApplication.shared.open(NSURL(string: usefullinksField.text!)! as URL, options: [:], completionHandler: nil)
        }
    }
    
    @objc func tap2(sender: UITapGestureRecognizer) {
        if(dictionaryLink.text != "") {
            UIApplication.shared.open(NSURL(string: dictionaryLink.text!)! as URL, options: [:], completionHandler: nil)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let tapGesture1 = UITapGestureRecognizer(target: self, action: #selector(self.tap1))

        let tapGesture2 = UITapGestureRecognizer(target: self, action: #selector(self.tap2))
        
        usefullinksField.addGestureRecognizer(tapGesture1)
        dictionaryLink.addGestureRecognizer(tapGesture2)
        self.counterLabel.isHidden = true;
        self.hideKeyboardWhenTappedAround()
        self.dictionaryLink.isHidden = true;
        self.usefullinksTitle.isHidden = true;
        self.usefullinksField.isHidden = true;
        self.meaningField.isHidden = true;
        self.meaningTitle.isHidden = true;
        self.descriptionField.isHidden = true;
        self.descriptionTitle.isHidden = true;
        self.codeField.isHidden = true;
        self.codeTitle.isHidden = true;
        self.webImage.isHidden = true;
        self.bigusefullinkstitle.isHidden = true;
        self.infoaboutsymbol.isHidden = true;
        self.waiting.isHidden = true;
        self.loading.isHidden = true;
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        var temp = UserDefaults.standard.string(forKey: "code")
        if (UserDefaults.standard.string(forKey: "code") != nil) {
            
            let db = Firestore.firestore();
            let docRef = db.collection("GardinerCode").document(UserDefaults.standard.string(forKey: "code")!)
            
            docRef.getDocument { (document, error) in
                if let document = document, document.exists {
                    let dataDescription = document.data().map(String.init(describing:)) ?? "nil"
                    print("Document data: \(dataDescription)")
                    
                    var description : String!
                    var image : String!
                    var link : String!
                    var meaning : String!
                    var code : String!
                    //getting the json response
                    code = document.documentID
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
                                self.usefullinksField.text = link;
                                self.usefullinksField.isHidden = false;
                                self.usefullinksTitle.isHidden = false;
                            }
                            self.meaningField.text = meaning;
                            
                            if (code.contains("A")) {
                                if (code.contains("Aa")) {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/unclassified/" ;
                                }
                                else {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/man-and-his-occupations/";
                                }
                            }
                            else
                                if (code.contains("B")) {
                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/woman-and-her-occupations/";
                                }
                                else
                                    if (code.contains("C")) {
                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/anthropomorphic-deities/";
                                    }
                                    else
                                        if (code.contains("D")) {
                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-the-human-body/";
                                        }
                                        else
                                            if (code.contains("E")) {
                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/mammals/";
                                            }
                                            else
                                                if (code.contains("F")) {
                                                    self.dictionaryLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-mammals/";
                                                }
                                                else
                                                    if (code.contains("G")) {
                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/birds/";
                                                    }
                                                    else
                                                        if (code.contains("H")) {
                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/parts-of-birds/";
                                                        }
                                                        else
                                                            if (code.contains("I")) {
                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/amphibious-animals-reptiles-etc/";
                                                            }
                                                            else
                                                                if (code.contains("K")) {
                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/fish-and-parts-of-fish/";
                                                                }
                                                                else
                                                                    if (code.contains("L")) {
                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/invertebrates-and-lesser-animals/";
                                                                    }
                                                                    else
                                                                        if (code.contains("M")) {
                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/trees-and-plants/";
                                                                        }
                                                                        else
                                                                            if (code.contains("N")) {
                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/sky-earth-water/";
                                                                            }
                                                                            else
                                                                                if (code.contains("O")) {
                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/buildings-parts-of-buildings-etc/";
                                                                                }
                                                                                else
                                                                                    if (code.contains("P")) {
                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/ships-and-parts-of-ships/";
                                                                                    }
                                                                                    else
                                                                                        if (code.contains("Q")) {
                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/domestic-and-funerary-furniture/";
                                                                                        }
                                                                                        else
                                                                                            if (code.contains("R")) {
                                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/temple-furniture-and-sacred-emblems/";
                                                                                            }
                                                                                            else
                                                                                                if (code.contains("S")) {
                                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/crowns-dress-staves-etc/";
                                                                                                }
                                                                                                else
                                                                                                    if (code.contains("T")) {
                                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/warfare-hunting-butchery/";
                                                                                                    }
                                                                                                    else
                                                                                                        if (code.contains("U")) {
                                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/agriculture-crafts-and-professions/";
                                                                                                        }
                                                                                                        else
                                                                                                            if (code.contains("V")) {
                                                                                                                self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/rope-fiber-baskets-bags-etc/";
                                                                                                            }
                                                                                                            else
                                                                                                                if (code.contains("W")) {
                                                                                                                    self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/vessels-of-stone-and-earthenware/";
                                                                                                                }
                                                                                                                else
                                                                                                                    if (code.contains("X")) {
                                                                                                                        self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/loaves-and-cakes/";
                                                                                                                    }
                                                                                                                    else
                                                                                                                        if (code.contains("Y")) {
                                                                                                                            self.dictionaryLink.text = "http://www.egyptianhieroglyphs.net/gardiners-sign-list/writings-games-music/";
                                                                                                                        }
                                                                                                                        else
                                                                                                                            if (code.contains("Z")) {
                                                                                                                                self.dictionaryLink.text =  "http://www.egyptianhieroglyphs.net/gardiners-sign-list/strokes/";
                            }
                            self.dictionaryLink.isHidden = false;
                            self.meaningField.isHidden = false;
                            self.meaningTitle.isHidden = false;
                            self.descriptionField.isHidden = false;
                            self.descriptionTitle.isHidden = false;
                            self.bigusefullinkstitle.isHidden = false;
                            self.infoaboutsymbol.isHidden = false;
                            self.codeField.isHidden = false;
                            self.codeTitle.isHidden = false;
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
            UserDefaults.standard.set(nil, forKey: "code")
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "viewall") {
            let DestVC: DictionaryViewController=segue.destination as! DictionaryViewController
            DestVC.arrayOfCells = self.arrayOfCells;
            DestVC.onetime = self.onetime;
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

