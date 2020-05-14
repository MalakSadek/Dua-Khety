//
//  TutorialViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 6/9/19.
//  Copyright © 2019 Malak Sadek. All rights reserved.
//

import UIKit

class TutorialViewController: UIViewController {

    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var tutorialImage: UIImageView!
    var tutorialcount:Int = 0
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tutorialImage.image = UIImage(named: "opening")
        UserDefaults.standard.set(false, forKey: "tutorial")
        doneButton.isHidden = true
        
        // Do any additional setup after loading the view.
    }
    
    @IBAction func nextButtonPressed(_ sender: Any) {
        if (tutorialcount < 20) {
        tutorialcount=tutorialcount+1;
        }
        buttonPressed()
    }
    @IBAction func previousButtonPressed(_ sender: Any) {
        if (tutorialcount > 0) {
        tutorialcount=tutorialcount-1;
        }
        buttonPressed()
    }
    
    func buttonPressed() {
        switch(tutorialcount) {
        case 1:
            tutorialImage.image = UIImage(named: "f0s1") ;
              doneButton.isHidden = true
            break;
        case 2:
            tutorialImage.image = UIImage(named: "f0s2");
            doneButton.isHidden = true
            break;
        case 3:
            tutorialImage.image = UIImage(named: "f1t");
            doneButton.isHidden = true
            break;
        case 4:
            tutorialImage.image = UIImage(named: "f1s1");
            doneButton.isHidden = true
            break;
        case 5:
            tutorialImage.image = UIImage(named: "f1s2");
            doneButton.isHidden = true
            break;
        case 6:
            tutorialImage.image = UIImage(named: "f1s3");
            doneButton.isHidden = true
            break;
        case 7:
            tutorialImage.image = UIImage(named: "f1s4");
            doneButton.isHidden = true
            break;
        case 8:
            tutorialImage.image = UIImage(named: "f2t");
            doneButton.isHidden = true
            break;
        case 9:
            tutorialImage.image = UIImage(named: "f2s1");
            doneButton.isHidden = true
            break;
        case 10:
            tutorialImage.image = UIImage(named: "f2s2");
            doneButton.isHidden = true
            break;
        case 11:
            tutorialImage.image = UIImage(named: "f3t");
            doneButton.isHidden = true
            break;
        case 12:
            tutorialImage.image = UIImage(named: "f3s1");
            doneButton.isHidden = true
            break;
        case 13:
            tutorialImage.image = UIImage(named: "f3s2");
            doneButton.isHidden = true
            break;
        case 14:
            tutorialImage.image = UIImage(named: "f4t");
            doneButton.isHidden = true
            break;
        case 15:
            tutorialImage.image = UIImage(named: "f4s1");
            doneButton.isHidden = true
            break;
        case 16:
            tutorialImage.image = UIImage(named: "f4s2");
            doneButton.isHidden = true
            break;
        case 17:
            tutorialImage.image = UIImage(named: "f5t");
            doneButton.isHidden = true
            break;
        case 18:
            tutorialImage.image = UIImage(named: "f5s1");
            doneButton.isHidden = true
            break;
        case 19:
            tutorialImage.image = UIImage(named: "f5s2");
            doneButton.isHidden = true
            break;
        case 20:
            tutorialImage.image = UIImage(named: "closing");
            doneButton.isHidden = false
            break;
        case 0:
            tutorialImage.image = UIImage(named: "opening");
             doneButton.isHidden = true
            break;
        default:
             tutorialImage.image = UIImage(named: "opening");
             doneButton.isHidden = true
        }
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        let source = UserDefaults.standard.integer(forKey: "source")
        
        if (source == 0) {
            UserDefaults.standard.set(true, forKey: "tutorial")
            performSegue(withIdentifier: "tutorialtoopening", sender: nil)
        } else if (source == 1) {
            performSegue(withIdentifier: "tutorialtosuccess", sender: nil)
        } else if (source == 2) {
            performSegue(withIdentifier: "tutorialtoonetime", sender: nil)
        }
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
