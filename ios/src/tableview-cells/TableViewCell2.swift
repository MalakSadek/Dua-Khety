//
//  TableViewCell2.swift
//  DuaKhety
//
//  Created by Malak Sadek on 2/22/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

import UIKit

class TableViewCell2: UITableViewCell {

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    @IBOutlet weak var owner: UILabel!
    @IBOutlet weak var code: UILabel!
    @IBOutlet weak var photo: UIImageView!
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
