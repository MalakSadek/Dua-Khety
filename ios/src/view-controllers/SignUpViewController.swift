//
//  SignUpViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import Firebase
import FirebaseAuth
import FirebaseFirestore

class SignUpViewController: UIViewController {

    @IBOutlet weak var nameField: UITextField!
    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var vpasswordField: UITextField!
    var Name:String = "";
    var Email:String = "";
    var Password:String = "";
    var vPassword:String = "";
    
    @IBAction func doneButtonPressed(_ sender: Any) {
      
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to sign up.", preferredStyle: .alert)
            
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
    
            if (self.nameField.hasText) {
                self.Name = self.nameField.text!;
            }
            else {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to enter your name!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
                
            }
            if (self.emailField.hasText) {
                self.Email = self.emailField.text!;
            }
            else {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to enter your email!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
                
            }
            if (self.passwordField.hasText) {
                self.Password = self.passwordField.text!;
            }
            else {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to enter your password!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
                
            }
            if (self.vpasswordField.hasText) {
                self.vPassword = self.vpasswordField.text!;
            }
            else {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to re-enter your password!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
                
            }
            
            if (self.Password != self.vPassword) {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "Your passwords do not match!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
            }
            
            let db = Firestore.firestore();
            var unique = 1;
            db.collection("Users").getDocuments() { (querySnapshot, err) in
                    if let err = err {
                        print("Error getting documents: \(err)")
                    } else {
                        for document in querySnapshot!.documents {
                            if ((document.data()["Owner"]! as? String)! == self.nameField.text) {
                                unique = 0;
                            }
                        }
                        
                        if (unique == 0) {
                            let alertVC = UIAlertController(title: "Username already taken!", message: "Please choose a different username as this one already belongs to another user.", preferredStyle: .alert)
                            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                            alertVC.addAction(alertActionCancel)
                            self.present(alertVC, animated: true, completion: nil)
                        }
                        else {
                            
                            Firebase.Auth.auth().createUser(withEmail: self.Email,
                                                            password: self.Password)
                            { (user, error) in
                                
                                if(error != nil){
                                    let alertVC = UIAlertController(title: "Error", message: "Something went wrong, account might already exist.", preferredStyle: .alert)
                                    
                                    let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                                    alertVC.addAction(alertActionCancel)
                                    self.present(alertVC, animated: true, completion: nil)
                                }else{
                                    Auth.auth().signIn(withEmail: self.Email, password: self.Password) {
                                        (user, error) in
                                        if let user = Auth.auth().currentUser {
                                            user.sendEmailVerification(completion: nil)
                                            let userDefaults = UserDefaults.standard
                                            userDefaults.set(self.Email, forKey: "Email");
                                            userDefaults.set(self.Password, forKey: "Password");
                                            userDefaults.set(self.Name, forKey: "Name");
                                            
                                            self.performSegue(withIdentifier: "Verification", sender: nil)
                                            
                                        }
                                    }
                                    
                                }
                            }
                        }
                    }
            }
            }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    open override var shouldAutorotate: Bool {
        get {
            return false
        }
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        get {
            return .portrait
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
