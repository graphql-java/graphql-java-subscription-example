# graphql-java Subscriptions over WebSockets example

An example of using graphql subscriptions via websockets, graphql-java, reactive-streams and RxJava.

Note: This wont currently build from Maven publicly because the subscription support is not on master yet.  

See https://github.com/graphql-java/graphql-java/pull/754

Check that out and install it locally via

    ./gradlew clean install -DRELEASE_VERSION=6.0-subscriptions


To build the example code type

    ./gradlew build
    
To run the example code type    
    
    ./gradlew run
    
Point your browser at 

    http://localhost:3000/  
    
# Code Explanation

This example shows how you can use graphql-java subscription support to "subscribe" to a publisher of events.
Then as events occur, graphql-java will map the original graphql query over those same event objects and send out
a stream of ExecutionResult objects.

So here we have stock update type system defined as

    type Subscription {
        stockQuotes(stockCodes:[String]) : StockPriceUpdate!
    }
    
    type StockPriceUpdate {
        dateTime : String
        stockCode : String
        stockPrice : Float
        stockPriceChange : Float
    }

We have a JavaScript client that sends a subscription graphql query over websockets to the server.

    var query = 'subscription StockCodeSubscription { \n' +
        '    stockQuotes {' +
        '       dateTime\n' +
        '       stockCode\n' +
        '       stockPrice\n' +
        '       stockPriceChange\n' +
        '     }' +
        '}';
    var graphqlMsg = {
        query: query,
        variables: {}
    };
    exampleSocket.send(JSON.stringify(graphqlMsg));
   
The server executes this with the graphql-java engine

        GraphQL graphQL = GraphQL
                .newGraphQL(graphqlPublisher.getGraphQLSchema())
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);

The result of that initial subscription query is a http://www.reactive-streams.org/ Publisher

        Publisher<ExecutionResult> stockPriceStream = executionResult.getData();
        
Under the covers a RxJava 2.x implementation is used to provide a stream of synthesized stock events.

Rxjava Flows are an implementation of the reactive streams Publisher interface.  You can use ANY reactive streams
implementation as a source.  graphql-java uses the reactive streams interfaces as a common interface.

See https://github.com/ReactiveX/RxJava for more information on RxJava.  
        
The server side code then subscribes to this publisher of events and sends the results back over the websocket
to the waiting browser client

        stockPriceStream.subscribe(new Subscriber<ExecutionResult>() {

            @Override
            public void onSubscribe(Subscription s) {
                subscriptionRef.set(s);
                request(1);
            }

            @Override
            public void onNext(ExecutionResult er) {
                log.debug("Sending stick price update");
                try {
                    Object stockPriceUpdate = er.getData();
                    getRemote().sendString(JsonKit.toJsonString(stockPriceUpdate));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request(1);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Subscription threw an exception", t);
                getSession().close();
            }

            @Override
            public void onComplete() {
                log.info("Subscription complete");
                getSession().close();
            }
        });

The selection set of fields named in the original query will be applied to each underlying stock update object.  

So we had a selection set as follows : 

        stockQuotes {
            dateTime
            stockCode
            stockPrice
            stockPriceChange
        }

The underling stock update object is mapped to this selection of fields, just like any normal graphql query.  The format of the results
on the browser is JSON, again like any other normal graphql query.
       


