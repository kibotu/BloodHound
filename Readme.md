BloodHoud [![](https://jitpack.io/v/kibotu/BloodHound.svg)](https://jitpack.io/#kibotu/BloodHound) [![Build Status](https://travis-ci.org/kibotu/BloodHound.svg)](https://travis-ci.org/kibotu/BloodHound)  [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)
----------------------

Tiny library for tracking screens and events with google analytics.

### How to install
	
	repositories {
	    maven {
	        url "https://jitpack.io"
	    }
	}
		
	dependencies {
            compile 'com.github.kibotu:BloodHound:-SNAPSHOT'
    }
    
### How to use

1. Initialize
    
        BloodHound.with(context, "trackingId")
                .enableDryRun(BuildConfig.DEBUG)
                .enableLogging(BuildConfig.DEBUG);
    
2. Track Screens

        BloodHound.track("screen");
        
        
3. Track Events
 
 
        BloodHound.track("screen", "category", "action", "label");
    
    
### Options (defaults)
    
    BloodHound.with(context, "trackingId")
            .enableExceptionReporting(false)
            .enableAdvertisingIdCollection(true)
            .enableAutoActivityTracking(false)
            .setSessionTimeout(300)
            .setSampleRate(100)
            .setLocalDispatchPeriod(300)
            .enableLogging(true)
            .enableDryRun(false)
            .setSessionLimit(500);
            
            
    BloodHound.reportActivityStart(activity);
    BloodHound.reportActivityStop(activity);