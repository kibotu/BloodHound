BloodHoud [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BloodHound-green.svg?style=true)](https://android-arsenal.com/details/1/3559) [![](https://jitpack.io/v/kibotu/BloodHound.svg)](https://jitpack.io/#kibotu/BloodHound) [![Build Status](https://travis-ci.org/kibotu/BloodHound.svg)](https://travis-ci.org/kibotu/BloodHound)  [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15) [![Licence](https://img.shields.io/badge/licence-Apache-blue.svg)](https://raw.githubusercontent.com/kibotu/BloodHound/master/LICENSE) [![Gradle Version](https://img.shields.io/badge/gradle-2.13-green.svg)](https://docs.gradle.org/current/release-notes)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
            
###License
<pre>
Copyright 2016 Jan Rabe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>