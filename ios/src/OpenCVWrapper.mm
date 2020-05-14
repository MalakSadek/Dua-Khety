//
//  OpenCVWrapper.m
//  DuaKhety
//
//  Created by Malak Sadek on 3/15/18.
//  Copyright © 2018 Malak Sadek. All rights reserved.
//

#import "OpenCVWrapper.h"
#import <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgcodecs/ios.h>
#import <UIKit/UIKit.h>
#include <iostream>
#include <map>
#include <queue>
//using namespace std;
using namespace cv;
@implementation OpenCVWrapper

Mat img_C, temp, edge_detected, img_BW, original, blurred, clean, test, test2, temptest;
Mat centres, stats, labels;
std::vector<Mat> imgs, imgsBW;

- (void)test:(UIImage *)image;
{
 
    Scalar avg;
    UIImageToMat(image, test);
//    cvtColor(temptest, test, CV_BGR2GRAY);
    //    equalizeHist(img_BW, img_BW);
    avg = mean(test);
    
    threshold(test, test2,  avg[0]*0.5, 255, THRESH_BINARY_INV);
    
    UIImageWriteToSavedPhotosAlbum(MatToUIImage(test2), nil, nil, nil);
    
    threshold(test, test2,  avg[0]*0.45, 255, THRESH_BINARY_INV);
    
    UIImageWriteToSavedPhotosAlbum(MatToUIImage(test2), nil, nil, nil);
    
    threshold(test, test2,  avg[0]*0.4, 255, THRESH_BINARY_INV);
    
    UIImageWriteToSavedPhotosAlbum(MatToUIImage(test2), nil, nil, nil);
    
    threshold(test, test2,  avg[0]*0.35, 255, THRESH_BINARY_INV);
    
    UIImageWriteToSavedPhotosAlbum(MatToUIImage(test2), nil, nil, nil);
    
}

- (NSMutableArray*)segment:(UIImage *)image;
{
    
    Scalar avg;
    UIImageToMat(image, original);
    UIImageToMat(image, clean);
    resize(original, img_C,  cv::Size(), 0.5, 0.5);
    resize(clean, clean,  cv::Size(0, 0), 0.5, 0.5);
    cvtColor(img_C, img_BW, CV_BGR2GRAY);
    avg = mean(img_BW);
    blur(img_BW, blurred, cv::Size(3, 3), cv::Point(-1, -1), BORDER_DEFAULT);
    threshold(img_BW, img_BW,  avg[0], 255, THRESH_BINARY_INV);
    Canny(blurred, edge_detected, (0.66*avg[0]), (1.33*avg[0]), 3);
    
    //CCL from here
    int no_of_Labels = connectedComponentsWithStats(edge_detected, labels, stats, centres, 8, CV_32S);


    for (int i = 0; i < no_of_Labels; i++)
    {
        int h = stats.at<int>(i, CC_STAT_HEIGHT), w = stats.at<int>(i, CC_STAT_WIDTH), t = stats.at<int>(i, CC_STAT_TOP), l= stats.at<int>(i, CC_STAT_LEFT);

        if (stats.at<int>(i, CC_STAT_AREA) > 2000 ||
            stats.at<int>(i, CC_STAT_AREA) < 100 ||
            h > img_C.rows / 2 || w > img_C.cols/2  ||
            stats.at<int>(i, CC_STAT_LEFT) <8 ||
            w<15|| h<15)
            continue;

        int x = centres.at<double>(i, 0), y = centres.at<double>(i, 1);

        rectangle(img_C, cvPoint(x - w / 2, y - h / 2), cvPoint(x + w / 2, (y + h / 2) + 10), Scalar(0, 0, 205), 0.5);
       // std::cout << "Point no, " << i << "(" <<
       // stats.at<int>(i, CC_STAT_TOP) <<
      //  "," << stats.at<int>(i, CC_STAT_LEFT) <<
       // ")" << ": height is " << h <<
      //  ", width is " << w << ", area is " <<
       // stats.at<int>(i, CC_STAT_AREA) << std::endl;

        //this is the cropping
        cv::Rect temp2(max(0,l-2), max(0,t-2), min(w+2, clean.cols), min(h+5, clean.rows));
        temp = clean(temp2);

        imgs.push_back(temp);
        resize(temp, temp, cv::Size(50, 75));
        Mat tempp;
        tempp = clean(temp2);
        cvtColor(tempp, tempp, CV_BGR2GRAY);
        Scalar avgg = mean(tempp);
        threshold(tempp, tempp,  avgg[0], 255, THRESH_BINARY_INV);

        imgsBW.push_back(tempp);


        resize(tempp, tempp, cv::Size(50, 75));
    }
    imgs.push_back(img_C);
    
    std::vector<UIImage*> images;
    std::vector<UIImage*> imagesBW;
    NSMutableArray *imagesarray = [NSMutableArray arrayWithCapacity:images.size()+imagesBW.size()];
    NSMutableArray *imagearray = [NSMutableArray arrayWithCapacity:images.size()];
    NSMutableArray *imagearrayBW = [NSMutableArray arrayWithCapacity:imagesBW.size()];
    
    for(int i = 0; i < imgs.size(); i++) {
        images.push_back(MatToUIImage(imgs.at(i)));
        [imagearray addObject:images.at(i)];
        if (i < imgsBW.size()) {
             imagesBW.push_back(MatToUIImage(imgsBW.at(i)));
                     [imagearrayBW addObject:imagesBW.at(i)];
        }
    }
    
    imagesarray[0] = imagearray;
    imagesarray[1] = imagearrayBW;
    
    return imagesarray;
    
}
@end
