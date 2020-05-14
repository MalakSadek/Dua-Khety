//
//  SignInViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseAuth
import Firebase
import FirebaseFirestore

class SignInViewController: UIViewController {

    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    @IBAction func signinButtonPressed(_ sender: Any) {
        
        let reachability = Reachability()!
        
        do {
            try reachability.startNotifier()
        } catch {
            print("Unable to start notifier")
        }
        reachability.whenUnreachable
            = { _ in
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to log in.", preferredStyle: .alert)
            
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
            if(!self.emailField.hasText) {
                let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to enter your email!", preferredStyle: .alert)
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
            }
            else {
                if (!self.passwordField.hasText) {
                    let alertVC = UIAlertController(title: "Incorrect Input", message: "You have to enter your password!", preferredStyle: .alert)
                    let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                    alertVC.addAction(alertActionCancel)
                    self.present(alertVC, animated: true, completion: nil)
                }
                else {
                    FirebaseAuth.Auth.auth().signIn(withEmail: self.emailField.text!, password: self.passwordField.text!)
                    { (user, error) in
                        if(error != nil){
                            let alertVC = UIAlertController(title: "Error", message: "Invalid email or password, please try again.", preferredStyle: .alert)
                            
                            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                            alertVC.addAction(alertActionCancel)
                            self.present(alertVC, animated: true, completion: nil)
                            print("INCORRECT signed in")
                        }
                        else{
                            let userDefaults = UserDefaults.standard
                            userDefaults.set(self.emailField.text, forKey: "Email");
                            userDefaults.set(self.passwordField.text, forKey: "Password");
                            
                            let db = Firestore.firestore();
   
                            db.collection("Users").whereField("Email", isEqualTo: self.emailField.text!)
                                .getDocuments() { (querySnapshot, err) in
                                    if let err = err {
                                        print("Error getting documents: \(err)")
                                    } else {
                                        for document in querySnapshot!.documents {
                                           userDefaults.set((document.data()["Owner"]! as? String)!, forKey: "Name")
                                           self.performSegue(withIdentifier: "Signin", sender: nil)
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
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
