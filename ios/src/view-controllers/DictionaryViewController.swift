//
//  DictionaryViewController.swift
//  DuaKhety
//
//  Created by Malak Sadek on 6/9/19.
//  Copyright © 2019 Malak Sadek. All rights reserved.
//

import UIKit

class DictionaryViewController: UIViewController,UITableViewDataSource,UITableViewDelegate {
    
    var onetime:Bool = true;
    var arrayOfCells:[cell] = []

    @IBOutlet weak var dictionaryview: UITableView!
    
    @IBAction func moreInfoButtonPressed(_ sender: Any) {
        let alertVC = UIAlertController(title: "Information on Sir Gardiner's Classification of Hieroglyphics:", message: "Each letter in the code represents a category, and each number represents a specific symbol.\n\nThe letter classification is as follows:\n\n" +
            "A - Man and his occupations\n\nB - Woman and her occupations\n\nC - Anthropomorphic deities\n\nD - Parts of the human body\n\nE - Mammals\n\nF - Parts of Mammals\n\nG - Birds\n\nH - Parts of Birds\n\nI - Amphibious animals, reptiles, etc.\n\nJ - None\n\nK - Fish and parts of fish\n\nL - Invertebrates and lesser animals\n\nM - Trees and plants\n\nN - Sky, earth, water\n\nO - Buildings, parts of buildings, etc.\n\nP - Ships and parts of ships\n\nQ - Domestics and funerary furniture\n\nR - Temple furniture and sacred emblems\n\nS - Crowns, dress, staves, etc.\n\nT - Warfare, hunting, and butchery\n\nU - Agriculture, crafts, and professions\n\nV - Rope, fiber, baskets, bags, etc.\n\nW - Vessels of stone and earthenware\n\nX - Loaves and cakes\n\nY - Writings, games, music\n\nZ - Strokes, signs derived from Hieratic, geometrical figures\n\nAa - Unclassified\n\n", preferredStyle: .alert)
        
        let alertActionCancel = UIAlertAction(title: "Got it!", style: .default, handler: nil)
        alertVC.addAction(alertActionCancel)
        self.present(alertVC, animated: true, completion: nil)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
         return arrayOfCells.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 300;
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
            let DestVC: SearchViewController=segue.destination as! SearchViewController
            DestVC.onetime = self.onetime;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.dictionaryview.dequeueReusableCell(withIdentifier: "cell") as! DictionaryViewCell
        
        cell.code.text = self.arrayOfCells[indexPath.row].code
        cell.picture.image = UIImage(data: self.arrayOfCells[indexPath.row].photo!)
        cell.desc.text = self.arrayOfCells[indexPath.row].description
        cell.meaning.text = self.arrayOfCells[indexPath.row].meaning

        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        UserDefaults.standard.set(self.arrayOfCells[indexPath.row].code!, forKey: "code")
        performSegue(withIdentifier: "viewalltosearch", sender: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
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
