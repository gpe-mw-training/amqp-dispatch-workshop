/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.gpte.amqp.examples.simplejms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;

public class AMQPQueueExample {

   private static final String CONNECTION_URL="CONNECTION_URL";
   private static final String QUEUE_NAME="QUEUE_NAME";

   public static void main(String[] args) throws Exception {
      Connection connection = null;

      String connectionUrl = System.getProperty(CONNECTION_URL);
      if(connectionUrl == null || connectionUrl.equals(""))
        throw new RuntimeException("main() must pass the following system property: "+CONNECTION_URL);

      String queueName = System.getProperty(QUEUE_NAME);
      if(queueName == null || queueName.equals(""))
        throw new RuntimeException("main() must pass the following system property: "+QUEUE_NAME);

      System.out.println("main() will connect to: "+connectionUrl+" : at the following queue: "+queueName);

      ConnectionFactory connectionFactory = new JmsConnectionFactory(connectionUrl);

      try {

         // Step 1. Create an amqp qpid 1.0 connection
         connection = connectionFactory.createConnection();

         // Step 2. Create a session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Step 3. Create a sender
         Queue queue = session.createQueue(queueName);
         MessageProducer sender = session.createProducer(queue);

         // Step 4. send a few simple message
         sender.send(session.createTextMessage("Hello world"));

         connection.start();

         // Step 5. create a moving receiver, this means the message will be removed from the queue
         MessageConsumer consumer = session.createConsumer(queue);

         // Step 7. receive the simple message
         TextMessage m = (TextMessage) consumer.receive(5000);
         System.out.println("\nmessage = " + m.getText()+"\n");

      } finally {
         if (connection != null) {
            // Step 9. close the connection
            connection.close();
         }
      }
   }
}
