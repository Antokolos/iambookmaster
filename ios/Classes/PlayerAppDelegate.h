//
//  PlayerAppDelegate.h
//  Player
//
//  Created by Gleb Gadyatskiy on 21/05/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PlayerViewController;

@interface PlayerAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    PlayerViewController *viewController;
	IBOutlet UIWebView *webView;

}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet PlayerViewController *viewController;
@property (nonatomic, retain) IBOutlet UIWebView *webView;
@end

