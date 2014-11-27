Project: Single Instance
Developer: Borja Lopez Urkidi, Krzysztof Otrebski
Organization: NEGU Soft
Web: http://www.negusoft.com

![Build status](https://travis-ci.org/otros-systems/Single-Instance.svg?branch=master "Build status")


This is a utility to control the creation of instances across the local system. This means we can check if there is already an instance of certain program running at the moment. It is also possible to implement actions to be performed when attempting to create a new instance if there was already one active.

A socket based mechanism is used to establish the instances. The port number is saved in file in temp directory. If new instance can to
connect on this port, meaning that there is another one running.
