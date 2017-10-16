# graphql-java Subscriptions over WebSockets example

An example of using graphql-java subscriptions via websockets.


To build the code type

    ./gradlew build
    
Note: This wont currently build from Maven publicly because the subscription support is on master yet.  

See https://github.com/graphql-java/graphql-java/pull/754

Check that out and run to install it locally

    ./gradlew clean install -DRELEASE_VERSION=6.0-subscriptions
    
    
To run the code type    
    
    ./gradlew run
    
Point your browser at 

    http://localhost:3000/    


