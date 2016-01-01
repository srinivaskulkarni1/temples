package com.temples.in.queue_processor;


public class App 
{
/*	private static String QUEUE_NAME;
	private static String QUEUE_HOST;
	private static String EXCHANGE_NAME;
	private static int THREADS = 5; 
	private static final String INGEST_ROUTING_KEY = "INGEST_ROUTING_KEY";
	private static ConnectionFactory factory;
	private static Connection connection;
	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	
	static {
		QUEUE_NAME = Configuration.getProperty(Configuration.QUEUE_NAME);
		QUEUE_HOST = Configuration.getProperty(Configuration.QUEUE_HOST);
		EXCHANGE_NAME = Configuration
				.getProperty(Configuration.INGEST_EXCHANGE);
		setNumThreads();
		factory = new ConnectionFactory();
		factory.setHost(QUEUE_HOST);
		factory.setAutomaticRecoveryEnabled(true);
		connect();
	}

	private static void setNumThreads() {
		String threads = Configuration.getProperty(Configuration.QUEUE_THREADS);
		if (threads != null) {
			try {
				THREADS = Integer.valueOf(threads);
			} catch (NumberFormatException e) {
				LOGGER.info(
						"Invalid property value for property | {} | expected integer but was String | defaulting to DEFAULT_THREADS(5)",
						Configuration.QUEUE_THREADS);

				THREADS = 5;
			}
		}
	}
	
	private static void connect() {
		LOGGER.info("Conntecting to message queue on host {}", QUEUE_HOST);
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			LOGGER.error(
					"Cannot open connection to message queue on host {}. Error message | {} ",
					QUEUE_HOST, e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"Timeout exception while connection to message queue on host {}. Error message | {} ",
					QUEUE_HOST, e.getLocalizedMessage());
		}
	}*/
	
 /*   public static void main( String[] args )
    {
		
		try {
			Channel throwAwayChannel = connection.createChannel();
			ExecutorService threadExecutor = Executors.newFixedThreadPool(5);
			QueueMessageConsumer messageConsumer = new QueueMessageConsumer(connection, throwAwayChannel, threadExecutor);
			
			//receive messages from a particular exchange
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, INGEST_ROUTING_KEY);
			channel.basicQos(1);
			
		} catch (IOException e) {
	        System.out.println("IOException Waiting for messages.");
	        			
		} 

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" [x] Received '" + message + "'");
              try{
            	  dowork(message);
              } catch (Exception e) {
				e.printStackTrace();
			}finally{
            	  //send ack to rabbit mq so that it can clear the message from the queue
            	  channel.basicAck(envelope.getDeliveryTag(), false);
              }
            }

			private void dowork(String message) throws Exception {
				System.out.println("processing message " + message);
				Thread.sleep(1000);
			}
          };
          
          try {
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
	        System.out.println("IOException consuming messages.");
		}
      
    }*/
}
