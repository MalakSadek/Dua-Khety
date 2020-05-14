//
//  OpeningViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import AVKit
import AVFoundation
import FirebaseAuth
class OpeningViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()

    }

    override func viewDidAppear(_ animated: Bool) {
        
        var tutorial = UserDefaults.standard.bool(forKey: "tutorial")
        
        if (!tutorial)  {
           UserDefaults.standard.set(0, forKey: "source")
            performSegue(withIdentifier: "openingtotutorial", sender: nil)
        }
        else {
            
            let userDefaults = UserDefaults.standard
            if(userDefaults.string(forKey: "Email") != nil &&
                userDefaults.string(forKey: "Password") != nil ) && userDefaults.string(forKey: "Name") != nil {
                
                Auth.auth().signIn(withEmail: userDefaults.string(forKey: "Email")!, password: userDefaults.string(forKey:  "Password")!) {
                    (user, error) in
                    
                    self.performSegue(withIdentifier: "notfirst", sender: nil);
                    // Do any additional setup after loading the view.
                }
            }
            else {
                
                self.performSegue(withIdentifier: "first", sender: nil);
            }
        }
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
