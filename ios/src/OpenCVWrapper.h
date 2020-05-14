//
//  OpenCVWrapper.h
//  DuaKhety
//
//  Created by Malak Sadek on 3/15/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface OpenCVWrapper : NSObject
- (NSMutableArray*)segment:(UIImage *)image;
- (void)test:(UIImage *)image;
@end
