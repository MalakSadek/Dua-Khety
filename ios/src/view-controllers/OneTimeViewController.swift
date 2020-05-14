//
//  OneTimeViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import MessageUI

class OneTimeViewController: UIViewController,  UIImagePickerControllerDelegate,
UINavigationControllerDelegate, MFMailComposeViewControllerDelegate {

    var image:UIImage!;
    
    @IBAction func infoButtonPressed(_ sender: Any) {
        UserDefaults.standard.set(2, forKey: "source")
        performSegue(withIdentifier: "onetimetotutorial", sender: nil)
    }
    
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
    
    @IBAction func developersButtonPressed(_ sender: Any) {
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
            let alertVC = UIAlertController(title: "Connection Error", message: "You need to connect to the internet to search hieroglyphs.", preferredStyle: .alert)
            
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
       performSegue(withIdentifier: "camera", sender: nil)
    }


    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        }
        
        // Do any additional setup after loading the view.

    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "Search") {
            let destVC: SearchViewController=segue.destination as! SearchViewController
                destVC.onetime = true;
        } else
            if (segue.identifier == "camera") {
                let DestVC: CameraViewController=segue.destination as! CameraViewController
                    DestVC.submit = 0;
                    DestVC.onetime = true;
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
