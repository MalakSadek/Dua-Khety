//
//  SubmitActivityViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/19/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit
import MessageUI

class SubmitActivityViewController: UIViewController,UIImagePickerControllerDelegate,
    UINavigationControllerDelegate, MFMailComposeViewControllerDelegate
 {
    @IBOutlet weak var pickedImage: UIImageView!
    @IBOutlet weak var codeField: UITextField!
    @IBOutlet weak var descriptionField: UITextField!
    var image:UIImage?
    var cropped:Int = 0;
    @IBAction func submitButtonPressed(_ sender: Any) {
        
        if (codeField.hasText) {
            if (pickedImage.image == nil) {
                let alertVC = UIAlertController(title: "Input Error", message: "You select an image!", preferredStyle: .alert)
                
                let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
                alertVC.addAction(alertActionCancel)
                self.present(alertVC, animated: true, completion: nil)
            }
            else {
                sendEmail()
            }
        }
        else {
            let alertVC = UIAlertController(title: "Input Error", message: "You must include a Gardiner's Code!", preferredStyle: .alert)
            
            let alertActionCancel = UIAlertAction(title: "Okay", style: .default, handler: nil)
            alertVC.addAction(alertActionCancel)
            self.present(alertVC, animated: true, completion: nil)
        }
    }
    
    func sendEmail() {
        if MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self;
            mail.setToRecipients(["malaksadek@aucegypt.edu", "agha@aucegypt.edu", "mohapard@aucegypt.edu", "mohamady996@aucegypt.edu"])
            mail.setSubject("Dua-Khety New Photo Submission")
            if (descriptionField.hasText) {
            mail.setMessageBody("Gardiner Code: "+codeField.text!+"\nDescription: "+descriptionField.text!, isHTML: false)
            }
            else {
                mail.setMessageBody("Gardiner Code: "+codeField.text!, isHTML: false)
            }
            let imageData: NSData = UIImagePNGRepresentation(pickedImage.image!)! as NSData
            mail.addAttachmentData(imageData as Data, mimeType: "image/png", fileName: "imageName")
            self.present(mail, animated: true, completion: nil)
        }
        
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true, completion: nil)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
        if(cropped != 0) {
        pickedImage.image = image;
        }
        }
        // Do any additional setup after loading the view.
    
    override func viewDidAppear(_ animated: Bool) {

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
