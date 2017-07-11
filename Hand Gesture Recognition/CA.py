# -*- coding: utf-8 -*-

# Convexity Approach Code
# Author: Pablo Barros
# More info: 
# Barros, Pablo, et al. "A dynamic gesture recognition and prediction system using the convexity approach." Computer Vision and Image Understanding (2016).
#http://www.sciencedirect.com/science/article/pii/S107731421630159X


import cv2
import numpy as np
import math
import random



def binaryImage(image):
        """

        Apply Otsu threshold to a given RGB image.
        
        
        Given a RGB image, the Otsu treshold technique [1] is applied using it's OpenCV implementation.
        
        [1] Otsu, Nobuyuki. "A threshold selection method from gray-level histograms." Automatica 11.285-296 (1975): 23-27.
    

    
        Parameters
        ----------
        arg1 : numpy.array
            RGB image as a numpy array.            
    
        Returns
        -------
        numpy.array
            resulting image after the application of the Otsu treshold.
    
        """    
        
        image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)       
     
        ret2,image = cv2.threshold(image,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU)

        return  image


def extractFeatureVector(points):
    
        """

        Extract the feature vector from a given set of points.
        
        
        Applies the Convexity Approach feature calculation to a set of given points. The points should be provided in the order: "outer-inner-outer".                  
    
        Parameters
        ----------
        arg1 : numpy.array
            Array of points ( each one a (x,y) value)            
    
        Returns
        -------
        numpy.array
            Resulting array with the features extracted from the list of points.
    
        """    
        
        
        
        features = []
        
        for i in range(len(points) -1 ):            
            if(i == 0):
                features.append(distanceCalculator(points[0],points[len(points) -2],points[1]))
            elif (i == len(points) - 2):
                features.append(distanceCalculator(points[len(points) -2],points[len(points) -3], points[0]))
            else:
                features.append(distanceCalculator(points[i],points[i -1], points[i + 1]))
            
        return features
        
    
def distanceCalculator( farPoint, point1, point2):
        """

        Calculates the Euclidian distance between the inner point and the two outer points.
        
        
        Parameters
        ----------
        arg1 : int
            first outer point
        arg2 : int
            inner point
        arg3 : int
            second outer point            
    
        Returns
        -------
        double
            distance
    
        """   
        
        term1 = math.pow( (point1[1] - point2[1]) ,2)
        term2 = math.pow( (point1[0] - point2[0]) ,2)
        vi = math.pow( (term1 + term2) ,0.5)
        ui = farPoint[0]*(point1[1] - point2[1]) + farPoint[1]*(point1[0] - point2[0]) +  point2[1]*point2[0] - point1[1]*point1[0]
        
        return ui / vi
        
def calculateAngle( point1, point2, point3, point4, biggestX, biggestY):
       
        """

        Calculates the angle between given points.
        
        
        Parameters
        ----------
        arg1 : int
            first point
        arg2 : int
            second point
        arg3 : int
            third point
        arg4 : int
            fourth point            
        arg5 : int
            largest value of X            
        arg6 : int
            largest value of Y                  
        Returns
        -------
        double
            angle
    
        """   
        
        angle =  math.atan2(point2[1] - point1[1], point2[0]/biggestX-point1[0]) - math.atan2(point4[1] - point3[1], point4[0]-point3[0])
        
        return math.atan2(math.sin(angle), math.cos(angle))

 
def amplitudeNormalization(features):
           
        """

        Normalize the amplitude of a given set of features
        
        Normalizes the amplitude of the features between 0 and 1.
        
        
        Parameters
        ----------
        arg1 : numpy.array
            List of features
        
        Returns
        -------
        numpy.array
            Normalized set of features
    
        """   
        
        features.sort()       
        newFeatures = []
        
        bigger = 0
        for feature in features:
            if(feature > bigger):
                bigger = feature
         
        for feature in features:            
            newFeatures.append(feature/bigger)
        
        
        return newFeatures 
       
       
def lenghtNormalization(features, lenght):
        
        """

         Set the lenght of a given set of features
        
        Set the lenght of a given set of features to a specific size
        
        
        Parameters
        ----------
        arg1 : numpy.array
            List of features
        arg2 : numpy.array
            Number of desired features
        
        Returns
        -------
        numpy.array
            Normalized set of features
    
        """  
        
        normalizedFeatures = []
        if(len(features) < lenght):
            normalizedFeatures = features
            while(len(normalizedFeatures) < lenght):
                normalizedFeatures.append(0)
        
        else:
            runner = (len(features) / lenght) + 1
            i = 0
            positionsToBeInserted = []
            while(i < len(features)):
                positionsToBeInserted.append(i)
                i += runner
            
            while(len(positionsToBeInserted) != lenght):
                i = random.randint(len(features))
                haveItem = False
                for h in positionsToBeInserted:
                    if(h == i): 
                        haveItem = True
                        
                if(not haveItem):
                    positionsToBeInserted.append(i)
           
            positionsToBeInserted.sort()
            for x in positionsToBeInserted:
                normalizedFeatures.append(features[x])
               
        return normalizedFeatures     
        
def doConvexityApproach(img, img2, lenght):
           
           
    """

         Apply the Convexity Approach in a given image
        
        Apply the Convexity approach in a given image. 
        First the method binarizes the image, then finds the hand contour, 
        search for the inner and outer points, calculate the final feature vector,
        and add the final angle.
        
        
        Parameters
        ----------
        arg1 : numpy.array
            image to be used for feature extraction
        arg2 : numpy.array
            Image to be used as a visualization of inner and outer points (usually the same image)
        arg3: 
            Number of features in the final array
        
        Returns
        -------
        (numpy.array,numpy.array)
            (Image with the inner and outer points plotted., feature vector).
        
        
    """
    thresh = binaryImage(img)


    contours,hierarchy = cv2.findContours(thresh,cv2.CHAIN_APPROX_NONE,cv2.RETR_LIST)
    cnt = contours[0]       
   
    for i in range(len(contours)):
        if(cv2.contourArea(cnt) < cv2.contourArea(contours[i])):
           cnt = contours[i]
    
                               
    hull = cv2.convexHull(cnt, clockwise=True, returnPoints = False)
              
    defects = cv2.convexityDefects(cnt,hull)     
    if defects != None:

        biggestX = 0 
        biggestY =  0             
        points = []
        centerPoints = []
        for i in range(defects.shape[0]):
            s,e,f,d = defects[i,0]
            start = tuple(cnt[s][0])
            end = tuple(cnt[e][0])
            far = tuple(cnt[f][0])
            points.append(start)
            points.append(far)
            points.append(end)         
            centerPoints.append(far)
            cv2.circle(img2,far,5,[255,0,0],-1)
            cv2.circle(img2,start,5,[0,0,255],-1)
            cv2.circle(img2,end,5,[0,255,0],-1)
            if(start[0] >= biggestX):
               biggestX = start[0]
            if(end[0] >= biggestX):
               biggestX = end[0]
            if(far[0] >= biggestX):
               biggestX = far[0]               
            if(start[1] >= biggestY):
               biggestY = start[1]
            if(end[1] >= biggestY):
               biggestY = end[1]
            if(far[1] >= biggestY):
               biggestY = far[1]
                                            
        lista = []
        lista.append(centerPoints)
       
        momentPoints2 = np.array(lista)
    
        moments = cv2.moments(momentPoints2)
        if(  moments['m00'] > 0):
            x = moments['m10'] / moments['m00']
            y = moments['m01'] / moments['m00']        
            central = tuple( [int(x),int(y)])
            cv2.circle(img2,central,1,[255,255,255],-1)
                
            finalAngle = 0        
                    
            for i in range(defects.shape[0]):
                s,e,f,d = defects[i,0]
                start = tuple(cnt[s][0])
                end = tuple(cnt[e][0])
                far = tuple(cnt[f][0])  
                
                initialPoint = tuple([central[0],0])
                finalAngle += calculateAngle(start, central, central, initialPoint,biggestX,biggestY)
                finalAngle += calculateAngle(far, central, central, initialPoint,biggestX,biggestY)
                finalAngle += calculateAngle(end, central, central, initialPoint,biggestX,biggestY)
            
            momentsList = []
            for d in cv2.HuMoments(moments):
                momentsList.append(d)
            

            points = extractFeatureVector(points)
               
            points = amplitudeNormalization(points)
            points = lenghtNormalization(points,lenght)
            
            points.append(finalAngle)    
            

    return img2, points