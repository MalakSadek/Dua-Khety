//
//  SuccessViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

//
//  SubmitActivityViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import MessageUI
import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage

class SuccessViewController: UIViewController, UIImagePickerControllerDelegate,
UINavigationControllerDelegate, MFMailComposeViewControllerDelegate {

    @IBAction func infoButtonPressed(_ sender: Any) {
        UserDefaults.standard.set(1, forKey: "source")
        performSegue(withIdentifier: "successtotutorial", sender: nil)
    }
    
    @IBOutlet weak var loading: UIImageView!
    var arrayOfOwners:[String] = []
    var arrayOfPhotos:[Data] = []
    var arrayOfCodes:[String] = []
    var image:UIImage?;
    @IBOutlet weak var waiting: UIActivityIndicatorView!
    
    func sendEmail() {
        if MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self;
            mail.setToRecipients(["malaksadek@aucegypt.edu", "agha@aucegypt.edu", "mohapard@aucegypt.edu", "mohamady996@aucegypt.edu"])
            mail.setSubject("Dua-Khety Feedback")
            self.present(mail, animated: true, completion: nil)
        }
        
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func developerButtonPressed(_ sender: Any) {
        let alertVC = UIAlertController(title: "This application and all underlying processes was developed by:", message: "Malak Sadek\nMohamed Badreldin\nAhmed El Agha\nMohamed Ghoneim", preferredStyle: .alert)
        let alertActionCancel = UIAlertAction(title: "Thanks Guys!", style: .cancel, handler: nil)
        let settingsAction = UIAlertAction(title: "Contact Us", style: .destructive) { (_) -> Void in
            self.sendEmail()
        }
        alertVC.addAction(alertActionCancel)
        alertVC.addAction(settingsAction)
        self.present(alertVC, animated: true, completion: nil)
    }
    
    @IBAction func creditsButtonPressed(_ sender: Any) {
        let alertVC = UIAlertController(title: "Application Credits:", message: "A special thanks to http://www.egyptianhieroglyphs.net/ for providing the Gardiner code's of hieroglyphics. And to Morris Franken & Jan Van Gemert for providing a dataset that made this classification possible.", preferredStyle: .alert)
        let alertActionCancel = UIAlertAction(title: "Okay", style: .cancel, handler: nil)
        alertVC.addAction(alertActionCancel)
        self.present(alertVC, animated: true, completion: nil)
    }
    
    
    @IBAction func searchButtonPressed(_ sender: Any) {
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to search hieroglyphics.", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
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
                    self.performSegue(withIdentifier: "Search", sender: nil)
        }
    }
    
    @IBAction func takePhotoButtonPressed(_ sender: Any) {
       performSegue(withIdentifier: "camera", sender: self)
    }
 
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        image = info[UIImagePickerControllerOriginalImage] as? UIImage
      
        dismiss(animated:true, completion: {
    
            
        })

    }
    
    @IBAction func feedButtonPressed(_ sender: Any) {
       // print(Reachability.NetworkReachable.self)
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
        = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to view feed.", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
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
         self.waiting.isHidden = false;
         self.loading.isHidden = false;
        var count = 0;
        let db = Firestore.firestore();
        let configuration = URLSessionConfiguration.default
        configuration.httpMaximumConnectionsPerHost = 100
        db.collection("SocialContent").getDocuments() { (querySnapshot, err) in
            if let err = err {
                print("Error getting documents: \(err)")
               // self.waiting.isHidden = true;
                // self.loading.isHidden = true;
            } else {
                for document in querySnapshot!.documents {
                    
                    let storage = Storage.storage()
                    let storageRef = storage.reference()
                    let photoRef = storageRef.child((document.data()["Photo"]! as? String)!+".bin")
                    
                    photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                        if error != nil {
                            // Uh-oh, an error occurred!
                           // self.waiting.isHidden = true;
                           // self.loading.isHidden = true;
                        } else {
                            // Data for "images/island.jpg" is returned
                            self.arrayOfPhotos.append(data!)
                            self.arrayOfCodes.append((document.data()["GardinerCode"]! as? String)!)
                            self.arrayOfOwners.append((document.data()["Owner"]! as? String)!)

                            if (count == 10){
                                self.performSegue(withIdentifier: "feed", sender: nil)
                            }
                            count = count+1
                        }
                    }
                }
            }
        }
    }
}
    
    @IBAction func historyButtonPressed(_ sender: Any) {
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to view history.", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
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
            self.waiting.isHidden = false;
            self.loading.isHidden = false;
            var count = 0;
            let db = Firestore.firestore();
            db.collection("SocialContent").whereField("Owner", isEqualTo: UserDefaults.standard.string(forKey: "Name")!) .getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("Error getting documents: \(err)")
                 //   self.waiting.isHidden = true;
//                    self.loading.isHidden = true;
                } else {
                    for document in querySnapshot!.documents {
                        
                        let storage = Storage.storage()
                        let storageRef = storage.reference()
                        let photoRef = storageRef.child((document.data()["Photo"]! as? String)!+".bin")
                        
                        photoRef.getData(maxSize: 1 * 1024 * 1024) { data, error in
                            if error != nil {
                                // Uh-oh, an error occurred!
                          //      self.waiting.isHidden = true;
                           //     self.loading.isHidden = true;
                            } else {
                                // Data for "images/island.jpg" is returned
                                self.arrayOfPhotos.append(data!)
                                self.arrayOfCodes.append((document.data()["GardinerCode"]! as? String)!)
                                self.arrayOfOwners.append((document.data()["Owner"]! as? String)!)
                                if (count == querySnapshot!.documents.count-1){
                                    self.performSegue(withIdentifier: "history", sender: nil)
                                }
                                count = count+1
                            }
                        }
                    }
                }
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "camerasubmit") {
            let DestVC: CameraViewController=segue.destination as! CameraViewController
                DestVC.submit = 1;
                DestVC.onetime = false;
        } else
            if (segue.identifier == "camera") {
                let DestVC: CameraViewController=segue.destination as! CameraViewController
                    DestVC.submit = 0;
                    DestVC.onetime = false;
            }
        else if (segue.identifier == "history") {
            let destVC: HistoryViewController=segue.destination as! HistoryViewController
            destVC.arrayOfOwners = self.arrayOfOwners;
            destVC.arrayOfCodes = self.arrayOfCodes;
            destVC.arrayOfPhotos = self.arrayOfPhotos;
                destVC.results = false;
                destVC.onetime = false;
        }
        else if (segue.identifier == "feed") {
            let destVC: FeedViewController=segue.destination as! FeedViewController
            destVC.arrayOfOwners = self.arrayOfOwners;
            destVC.arrayOfCodes = self.arrayOfCodes;
            destVC.arrayOfPhotos = self.arrayOfPhotos;
        }
        else if (segue.identifier == "Search") {
            let destVC: SearchViewController=segue.destination as! SearchViewController
            destVC.onetime = false;
        }
         self.waiting.isHidden = true;
    }
    
    
    @IBAction func submitButtonPressed(_ sender: Any) {
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to submit a photo.", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
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
            self.performSegue(withIdentifier: "camerasubmit", sender: nil)
        }
    }
    @IBAction func logoutButtonPressed(_ sender: Any) {
        
        
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to log out.", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
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
            do {
                try  Auth.auth().signOut()
            } catch {
                print(error)
            }

            let alertVC = UIAlertController(title: "Gone So Soon?", message: "Are you sure you want to log out?", preferredStyle: .alert)
            let alertActionOkay = UIAlertAction(title: "Yes, I'm Sure", style: .default) {
                (_) in
                let userDefaults = UserDefaults.standard
                userDefaults.removeObject(forKey: "Email")
               userDefaults.removeObject(forKey: "Password")
                 userDefaults.removeObject(forKey: "Name")
                self.performSegue(withIdentifier: "Logout", sender: self)
            }
            alertVC.addAction(alertActionOkay)
            let alertActionCancel = UIAlertAction(title: "No, Cancel", style: .default, handler: nil)
            alertVC.addAction(alertActionCancel)
            self.present(alertVC, animated: true, completion: nil)
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        self.waiting.isHidden = true;
        self.loading.isHidden = true;
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
