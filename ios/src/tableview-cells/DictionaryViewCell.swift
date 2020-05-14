//
//  DictionaryViewCell.swift
//  DuaKhety
//
//  Created by Malak Sadek on 6/9/19.
//  Copyright © 2019 Malak Sadek. All rights reserved.
//

import UIKit

class DictionaryViewCell: UITableViewCell {

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    @IBOutlet weak var code: UILabel!
    @IBOutlet weak var picture: UIImageView!
    @IBOutlet weak var desc: UILabel!
    @IBOutlet weak var meaning: UILabel!
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
