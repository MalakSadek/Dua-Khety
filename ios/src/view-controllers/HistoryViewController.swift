//
//  HistoryViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseFirestore
import FirebaseStorage

class HistoryViewController: UIViewController,UITableViewDataSource,UITableViewDelegate {
    
    @IBOutlet weak var symbols: UIImageView!
    @IBOutlet weak var history: UIImageView!
    @IBOutlet weak var tv: UITableView!
    var arrayOfOwners:[String] = []
    var arrayOfPhotos:[Data] = []
    var arrayOfCodes:[String] = []
    var arrayOfImages:[UIImage] = []
    var chosenIndex:Int?
    var results:Bool = false
    var onetime:Bool = false
    @IBOutlet weak var clearhistoryButton: UIButton!
    
    @IBAction func backButtonPressed(_ sender: Any) {
        arrayOfImages.removeAll()
        arrayOfCodes.removeAll()
        arrayOfPhotos.removeAll()
        arrayOfOwners.removeAll()
        tv.reloadData()
        
        if (results) {
            if (onetime) {
            performSegue(withIdentifier: "OneTimeH", sender: nil)
            }
            else {
            performSegue(withIdentifier: "SuccessH", sender: nil)
            }
        }
        else {
            performSegue(withIdentifier: "SuccessH", sender: nil)
        }
    }
    
    @IBAction func clearhistoryButtonPressed(_ sender: Any) {
        
        let alertVC = UIAlertController(title: "Are you sure?", message: "By tapping Delete All you will permanently remove all items from your history!", preferredStyle: .alert)
        
        let alertActionCancel = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        let settingsAction = UIAlertAction(title: "Delete All", style: .destructive) { (_) -> Void in
            
            let db = Firestore.firestore();
            db.collection("SocialContent").whereField("Owner", isEqualTo: UserDefaults.standard.string(forKey: "Name")!) .getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("Error getting documents: \(err)")
                } else {
                    for document in querySnapshot!.documents {
                        document.reference.delete()
                    }
                }
            }
            
            self.arrayOfImages.removeAll()
            self.arrayOfPhotos.removeAll()
            self.arrayOfCodes.removeAll()
            self.arrayOfOwners.removeAll()
            self.tv.reloadData()
            self.view.reloadInputViews()
            
        }
        alertVC.addAction(settingsAction)
        alertVC.addAction(alertActionCancel)
        self.present(alertVC, animated: true, completion: nil)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayOfCodes.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tv.dequeueReusableCell(withIdentifier: "cell") as! TableViewCell
      
        cell.code.text = self.arrayOfCodes[indexPath.row]
      
        if(!self.results) {
            cell.Photograph.image = UIImage(data: self.arrayOfPhotos[indexPath.row])
        }
        else {
            cell.Photograph.image = self.arrayOfImages[indexPath.row]
        }
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
      
        if (segue.identifier == "moreinfo") {
            let destVC: ResultsViewController=segue.destination as! ResultsViewController
            if (!results) {

            destVC.pickedDataImage = UIImage(data: arrayOfPhotos[chosenIndex!]);
            destVC.isresults = false
            }
            else {
                destVC.image = arrayOfImages[chosenIndex!]
                destVC.isresults = true
            }
            destVC.onetime = self.onetime
            destVC.results = self.arrayOfCodes[chosenIndex!];
            destVC.arrayOfCodes = self.arrayOfCodes;
            destVC.arrayOfImages = arrayOfImages;
        }
        else if (segue.identifier == "Analyze"){
            let destVC: AnalyzingViewController=segue.destination as! AnalyzingViewController
            destVC.pickedImageData = UIImage(data: arrayOfPhotos[chosenIndex!]);
            destVC.onetime = self.onetime
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 300;
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (!results) {
        let alertVC = UIAlertController(title: "What would you like to do?", message: "Please choose an option!", preferredStyle: .alert)
        
        let alertActionCancel = UIAlertAction(title: "Cancel", style: .cancel) {(_) -> Void in
            
              self.tv.deselectRow(at: indexPath, animated: true)
        }
        
        let alertActionInfo = UIAlertAction(title: "More Info", style: .default) {(_) -> Void in
            self.chosenIndex = indexPath.row
            self.performSegue(withIdentifier: "Analyze", sender: self)
        }
        
        
        
        let settingsAction = UIAlertAction(title: "Delete", style: .destructive) { (_) -> Void in
            
            let db = Firestore.firestore();
            db.collection("SocialContent").whereField("GardinerCode", isEqualTo: self.arrayOfCodes[indexPath.row]).getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("Error getting documents: \(err)")
                } else {
                    for document in querySnapshot!.documents {
                        document.reference.delete()
                    }
                }
            }
            
            self.arrayOfPhotos.remove(at: indexPath.row)
            self.arrayOfCodes.remove(at: indexPath.row)
            self.arrayOfOwners.remove(at: indexPath.row)
            self.tv.deleteRows(at: [indexPath], with: .automatic)
            self.tv.reloadData()
            self.view.reloadInputViews()
        
        }
    
        alertVC.addAction(alertActionCancel)
        alertVC.addAction(alertActionInfo)
        alertVC.addAction(settingsAction)
        self.present(alertVC, animated: true, completion: nil)
        } else {
            self.chosenIndex = indexPath.row
           self.performSegue(withIdentifier: "moreinfo", sender: nil)
        }
    }
    
  
    override func viewWillAppear(_ animated: Bool) {
        self.hideKeyboardWhenTappedAround()
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        tv.reloadData()
        if (results) {
            history.isHidden = false
            symbols.isHidden = false
            clearhistoryButton.isHidden = true
        } else {
            clearhistoryButton.isHidden = false
            history.isHidden = true
            symbols.isHidden = true
        }
  
        // Do any additional setup after loading the view.
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
