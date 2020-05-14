//
//  VerificationViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import FirebaseAuth
import Firebase
import FirebaseFirestore

class VerificationViewController: UIViewController {

    
    @IBOutlet weak var spinner: UIActivityIndicatorView!
    @IBOutlet weak var verifyButton: UIButton!
    

    @IBAction func verifyButtonPressed(_ sender: Any) {
        let userDefaults:UserDefaults = UserDefaults.standard
        Auth.auth().signIn(withEmail: userDefaults.string(forKey: "Email")!, password: userDefaults.string(forKey: "Password")!) {
            (user, error) in
        if let user = Auth.auth().currentUser {
            if !user.isEmailVerified{

                let alertVC = UIAlertController(title: "Error", message: "Sorry. Your email address has not yet been verified. Please click on the link in the email sent to you.", preferredStyle: .alert)
                let alertActionOkay = UIAlertAction(title: "Okay", style: .default) {
                    (_) in
                    }
                
                alertVC.addAction(alertActionOkay)
                self.present(alertVC, animated: true, completion: nil)
    
            } else {
                
                let db = Firestore.firestore();
                var ref: DocumentReference? = nil
                ref = db.collection("Users").addDocument(data: [
                    "Email": userDefaults.string(forKey: "Email")!,
                    "Owner": userDefaults.string(forKey: "Name")!
                ]) { err in
                    if let err = err {
                        print("Error adding document: \(err)")
                    } else {
                        print("Document added with ID: \(ref!.documentID)")
                        self.performSegue(withIdentifier: "Success", sender: nil);
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
