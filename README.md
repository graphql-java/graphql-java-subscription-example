# graphql-java-subscription-example

An example of using graphql-java in a HTTP application with server side
subscriptions.


To build the code type

    ./gradlew build
    
To run the code type    
    
    ./gradlew run
    
Point your browser at 

    http://localhost:3000/    


Some example graphql queries might be

     {
       hero {
         name
         friends {
           name
           friends {
             id
             name
           }
           
         }
       }
     }


or maybe

    {
      luke: human(id: "1000") {
        ...HumanFragment
      }
      leia: human(id: "1003") {
        ...HumanFragment
      }
    }
    
    fragment HumanFragment on Human {
      name
      homePlanet
      friends {
        name
        __typename
      }
    }


