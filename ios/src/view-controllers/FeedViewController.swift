//
//  FeedViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseStorage
import FirebaseFirestore

class FeedViewController: UIViewController,UITableViewDataSource,UITableViewDelegate {
    
    @IBOutlet weak var recentslabel: UIImageView!
    @IBOutlet weak var usernamelabel: UIImageView!
    @IBOutlet weak var codeLabel: UIImageView!
    var arrayOfOwners:[String] = []
    var arrayOfPhotos:[Data] = []
    var arrayOfCodes:[String] = []
    @IBOutlet weak var tv: UITableView!
    @IBOutlet weak var segment: UISegmentedControl!
    @IBOutlet weak var searchBox: UITextField!
    @IBOutlet weak var searchButton: UIButton!
    var chosenIndex:Int?
    
    func search (type: Int, value: String) {
        
        if(type == 0) {
            let db = Firestore.firestore();
            var count = 0
            db.collection("SocialContent").whereField("Owner", isEqualTo: value).getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("Error getting documents: \(err)")
                } else {
                    for document in querySnapshot!.documents {
                        
                        let storage = Storage.storage()
                        let storageRef = storage.reference()
                        let photoRef = storageRef.child((document.data()["Photo"]! as? String)!+".bin")
                        
                        photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                            if error != nil {
                                // Uh-oh, an error occurred!
                                print("Error getting images: \(error)")
                            } else {
                                // Data for "images/island.jpg" is returned
                                self.arrayOfPhotos.append(data!)
                                self.arrayOfCodes.append((document.data()["GardinerCode"]! as? String)!)
                                self.arrayOfOwners.append((document.data()["Owner"]! as? String)!)
                                if (count == querySnapshot!.documents.count-1){
                                    self.tv.reloadData()
                                }
                                count = count+1
                            }
                        }
                    }
                }
            }
        }
        else if (type == 1) {
            let db = Firestore.firestore();
            var count = 0
            db.collection("SocialContent").whereField("GardinerCode", isEqualTo: value).getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("Error getting documents: \(err)")
                } else {
                    for document in querySnapshot!.documents {
                        
                        let storage = Storage.storage()
                        let storageRef = storage.reference()
                        let photoRef = storageRef.child((document.data()["Photo"]! as? String)!+".bin")
                        
                        photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                            if error != nil {
                                // Uh-oh, an error occurred!
                                print("Error getting images: \(error)")
                            } else {
                                // Data for "images/island.jpg" is returned
                                self.arrayOfPhotos.append(data!)
                                self.arrayOfCodes.append((document.data()["GardinerCode"]! as? String)!)
                                self.arrayOfOwners.append((document.data()["Owner"]! as? String)!)
                                if (count == querySnapshot!.documents.count-1){
                                    self.tv.reloadData()
                                }
                                count = count+1
                            }
                        }
                    }
                }
            }
        }
    }
    
    @IBAction func searchButtonPressed(_ sender: Any) {
        arrayOfOwners.removeAll()
        arrayOfPhotos.removeAll()
        arrayOfCodes.removeAll()
        self.tv.reloadData()
        
        if (searchBox.hasText) {
            if (segment.selectedSegmentIndex == 0) {
                
                usernamelabel.isHidden = false;
                codeLabel.isHidden = true;
                recentslabel.isHidden = true;
                search(type: 0, value: self.searchBox.text!)
              
               
            } else if (segment.selectedSegmentIndex == 1){

                codeLabel.isHidden = false;
                usernamelabel.isHidden = true;
                recentslabel.isHidden = true;
                search(type: 1, value: self.searchBox.text!)
                
            }
        }
        else {
            
            let alertVC = UIAlertController(title: "Input Error", message: "You have to enter a name or code!", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
            alertVC.addAction(alertActionCancel)
            self.present(alertVC, animated: true, completion: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
         if (segue.identifier == "Analyze2"){
            let destVC: AnalyzingViewController=segue.destination as! AnalyzingViewController
            destVC.pickedImageData = UIImage(data: arrayOfPhotos[chosenIndex!]);
            destVC.onetime = false;
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayOfOwners.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tv.dequeueReusableCell(withIdentifier: "cell2") as! TableViewCell2
        
        cell.photo.image = UIImage(data: arrayOfPhotos[indexPath.row])
        cell.owner.text = self.arrayOfOwners[indexPath.row]
        cell.code.text = self.arrayOfCodes[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
            self.chosenIndex = indexPath.row
            self.performSegue(withIdentifier: "Analyze2", sender: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        let attr = NSDictionary(object: UIFont(name: "BaronNeueBold", size: 16.0)!, forKey: NSAttributedStringKey.font as NSCopying)
        UISegmentedControl.appearance().setTitleTextAttributes(attr as [NSObject : AnyObject], for: .normal)
        codeLabel.isHidden = true;
        usernamelabel.isHidden = true;
        // Do any additional setup after loading the view.
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 300;
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
