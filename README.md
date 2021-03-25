# Dua-Khety
A mobile application to translate hieroglyphics (2018)

𓂀𓀀 A Mobile Application That Detects, Classifies & Transliterates Hieroglyphics.

![picture alt](https://github.com/MalakSadek/Dua-Khety/blob/master/analyzinglogo.png "Opening Logo")

# Abstract
Much of the way of life of the ancient Egyptians had been lost as no one was able to decipher the meaning of most of their symbols, Sir Alan Gardiner classified each hieroglyphic with a code that can then be used to search for it to know more about it’s meaning. This hieroglyphic dictionary ended up being thousands of pages long and looking anything up became a tedious task. Even with the emergence of the internet, there still remained the problem of not knowing a hieroglyphic’s Gardiner code to search for it.

To help solve this problem, Dua-Khety has been created as a mobile application, available on both `Android` (implemented in `Java`) and `iOS` (implemented in `Swift` & `Objective C`) devices, with the aim of detecting and classifying hieroglyphics from within a taken image and providing the user with their Gardiner codes as well as information about their transliteration and meaning. The entire process takes place offline, meaning that the service is convenient, fast, easy to use, and can be accessed anywhere at anytime.

# Features
There are many features to the application aside from detection and classification, It also includes A Hieroglyphics dictionary, which allows users to enter a Gardiner’s code and receive information about the hieroglyph it represents, A search history to view and re-analyze previous images, A social feed to view and analyze other users’ images and search for images by username or ID code, and A photo submission system that allows users to send the developers images of hieroglyphs with their Gardiner code and description to improve the classifier and thus improve the app’s performance in the future. These features rely on `MySQL databases` stored on a remote server and manipluated using PHP scripts.This was later migrated to `Google's Firebase` service for storage and database solutions.

Each feature is implemented in a separate view, and processes that are invisible to the user such as classification, each have their own view as well. The mobile application can communicate with two databases on a remote server. One holds social content used by the history and social feed features and the other holds information on hieroglyphics sorted by Gardiner codes, used by the hieroglyphics dictionary and to provide more information upon classification.

## Segmentation
For segmentation, this is accomplished using `OpenCV` by converting the bitmap taken by the user into a Mat object, turning it to black and white and then blurring the image with a radius of 3. The average is then computed. Afterwards, the image is thresholded with a min-val of the computed average and a maxval of 255. Canny is then applied with thresholds of the average * 0.66 and the average * 1.33 and an aperture size of 3 which was found to be optimal. Afterwards, the components are extracted and are used to draw bounding boxes around individual hieroglyphics on the original image. These are then cropped around and placed in an array of smaller images. The cropped images (from the original image) are then turned to black and white and thersholded in the same manner before being fed to the classifier to maximize accuracies.

## Classification
The classifier uses the concept of **Siamese networks**. Such a network differs from normal ones by taking as an input pairs of images and a label representing whether they are from the same class or not (displayed as a 0 or 1). In other words, half of what is fed into the network are pairs of images of the same classes with their label as 1, and the other half, two different images from two different classes, with their label as 0. The images are chosen randomly. Recently, this concept has been proven to be helpful in problems using a large number of classes with a small number of images for each class, As for classifying the actual test image, the training images are fed through the network and a feature vector of 640 values is extracteD. Afterwards, the average of all the vectors of the images in a class, is computed and stored in a Comma Separated Values (CSV) file. The same is done for all the classes (157). Coming down to predicting the class number for a new test image, It is fed into the same network and a feature vector of 640 values is extracted as well. Following this, the L1 distance between its feature vector and the previously extracted ones from each class are compared and the smallest five distances are taken as the top five predictions. The iOS application uses `OpenML` while the Android application uses `Tensorflow`.

***The accuracy for the top prediction is 66%, while the accuracy for the top five predictions is 88%.***

![picture alt](https://github.com/MalakSadek/Dua-Khety/blob/master/Thesis%20Poster.png "Thesis Poster")

Screenshots and videos can be found here: https://malaksadek.wordpress.com/2019/10/15/teeet-the-egyptian-microbus-experience/

# Download the App

The app is available on:
* The iOS App Store: https://itunes.apple.com/ng/app/dua-khety/id1466697097?mt=8&ign-mpt=uo%3D2
* The Google Play Store: https://play.google.com/store/apps/details?id=malaksadek.duakhety

# Contact

* email: mfzs1@st-andrews.ac.uk
* LinkedIn: www.linkedin.com/in/malak-sadek-17aa65164/
* website: http://malaksadekapps.com/
