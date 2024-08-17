package com.danielkleyman.jobsearchlnk.gpt;

public final class Constants {
    private Constants() {
    }

    public static final String task1 = "i will give you my CV and job description. give me in % a match between CV and job description, that means on how % cv matches this job. The following words significantly increase the score: software, engineer, developer, backend, back end, back-end, full stack, full-stack, fullstack, java, spring, microservice, object oriented, מתחנת, מפתח, javascript. If job description is not in english, then you should translate it in english by yourself and do the task. You answer must be only in 2 digits, no more words and signs. This is CV: ";
    public static final String task2 = " And this is job description: ";
    public static final String jobDescription = "Job Description Summary:\n" +
            "\n" +
            "What You Need To Know About The Role\n" +
            "\n" +
            "The role is inside the Big Data Developers group, which works with data scientists on complex AI models to ensure each of PayPal's multiple solutions will provide a smooth, confident and accessible working experience for our customers - both merchants and consumers! Some of the algorithms group members implement are ones that the industry does not have an answer to yet, which makes group members really fascinated about what they do.\n" +
            "\n" +
            "Meet our team\n" +
            "\n" +
            "The team comprises Machine Learning engineers that work very closely with Global Data Sciences (GDS) group. The joint work consists of developing PayPal's Analytical infra and best-of-breed fraud-detection, NLP, graph, clustering, and more Machine Learning algorithms using cutting-edge technologies.\n" +
            "\n" +
            "You will join a versatile team of engineers who are passionate about everything related to data and algorithms. We care not only about what we do, but also about each other. Everyone is encouraged to speak their minds and share ideas and we make sure to have fun while solving the complex challenges that are headed our way. We cherish our success and learn from failures, and we enjoy it when everyone brings their own selves.\n" +
            "\n" +
            "Job Description:\n" +
            "\n" +
            "Your way to impact\n" +
            "\n" +
            "In this role, you will directly impact the amount of prevented loss for the company and its consumers and take a meaningful role in making the PayPal platform safe, smooth, and accessible to our hundreds of millions of customers around the world.\n" +
            "\n" +
            "Your day-to-day\n" +
            "\n" +
            "In your day-to-day role, you will be part of a tremendous effort by the group to migrate part of Machine Learning solutions from the on-prem environment to Google Cloud. Some of the tasks you will be responsible for:\n" +
            "\n" +
            "Rebuilding or building massive Big Data analytical solutions utilizing graph, machine learning, and text mining algorithms.\n" +
            "Rebuilding or building foundational tools assisting migrations, leveraging Big Data industry standards and cutting-edge frameworks.\n" +
            "Working with analysts to extract meaningful and actionable insights from PayPal data.\n" +
            "Leading analytical projects from inception through research, development, and production on PayPal's data processing infrastructure.\n" +
            "\n" +
            "\n" +
            "What Do You Need To Bring\n" +
            "\n" +
            "B.Sc. in computer sciences/ mathematics or equivalent\n" +
            "Proven development experience in Java or/ Scala\n" +
            "Hands-on experience with different database solutions (SQL/NOSQL)\n" +
            "2+ years of experience building production software systems\n" +
            "Excellent English (written and verbal)\n" +
            "\n" +
            "\n" +
            "Nice to have (but not a must):\n" +
            "\n" +
            "Experience with Spark or other distributed data processing engine\n" +
            "Familiar with graph, clustering or other ML algorithms\n" +
            "Linux / other *nix - hands-on experience\n" +
            "We know the confidence gap and imposter syndrome can get in the way of meeting spectacular candidates. Please don't hesitate to apply.\n" +
            "\n" +
            "\n" +
            "Additional Job Description:\n" +
            "\n" +
            "Subsidiary:\n" +
            "\n" +
            "PayPal\n" +
            "\n" +
            "Travel Percent:\n" +
            "\n" +
            "0\n" +
            "\n" +
            "For the majority of employees, PayPal's balanced hybrid work model offers 3 days in the office for effective in-person collaboration and 2 days at your choice of either the PayPal office or your home workspace, ensuring that you equally have the benefits and conveniences of both locations.\n" +
            "\n" +
            "Our Benefits:\n" +
            "\n" +
            "At PayPal, we're committed to building an equitable and inclusive global economy. And we can't do this without our most important asset-you. That's why we offer benefits to help you thrive in every stage of life. We champion your financial, physical, and mental health by offering valuable benefits and resources to help you care for the whole you.\n" +
            "\n" +
            "We have great benefits including a flexible work environment, employee shares options, health and life insurance and more. To learn more about our benefits please visit https://www.paypalbenefits.com.\n" +
            "\n" +
            "Who We Are:\n" +
            "\n" +
            "Click Here to learn more about our culture and community.\n" +
            "\n" +
            "Commitment to Diversity and Inclusion\n" +
            "\n" +
            "PayPal provides equal employment opportunity (EEO) to all persons regardless of age, color, national origin, citizenship status, physical or mental disability, race, religion, creed, gender, sex, pregnancy, sexual orientation, gender identity and/or expression, genetic information, marital status, status with regard to public assistance, veteran status, or any other characteristic protected by federal, state, or local law. In addition, PayPal will provide reasonable accommodations for qualified individuals with disabilities. If you are unable to submit an application because of incompatible assistive technology or a disability, please contact us at paypalglobaltalentacquisition@paypal.com.\n" +
            "\n" +
            "Belonging at PayPal:\n" +
            "\n" +
            "Our employees are central to advancing our mission, and we strive to create an environment where everyone can do their best work with a sense of purpose and belonging. Belonging at PayPal means creating a workplace with a sense of acceptance and security where all employees feel included and valued. We are proud to have a diverse workforce reflective of the merchants, consumers, and communities that we serve, and we continue to take tangible actions to cultivate inclusivity and belonging at PayPal.\n" +
            "\n" +
            "Any general requests for consideration of your skills, please Join our Talent Community.\n" +
            "\n" +
            "We know the confidence gap and imposter syndrome can get in the way of meeting spectacular candidates. Please don't hesitate to apply.";
    public static final String CV = "Experienced software developer specializing in backend development and microservice- based solutions. My expertise in Java, Spring Framework and Microservices Architecture enables me to design, architect, and implement scalable, high-performance applications. My journey in tech is driven by a curiosity for AI and its applications in software development.\n" +
            "Skills\n" +
            "Programming languages: Java, JavaScript, TypeScript, HTML, Cascading Style Sheets (CSS).\n" +
            "Frameworks and technologies: Spring Framework, Microservices Architecture, Kafka, Kubernetes, Docker, Redis, CI/CD, Spring Boot, Rest Controller, Spring Data MongoDB, Spring Data JPA, Hibernate, Spring Security, Maven, CRUD Operations, Mongo Aggregation, Multithreading, Object Oriented Programming (OOP), Service Oriented Architecture, Firebase, Material UI, REST API, Agile. \n" +
            "Web: React, Redux, NPM, Node.js. Databases: MySQL, PostgreSQL, MongoDB, Redis.\n" +
            "Testing: JUnit, Mockito, Postman, Testcontainers, Swagger. IDEs: IntelliJ IDEA, Eclipse, VS Code. \n" +
            "Version control: GitHub.\n" +
            "Work experience\n" +
            "2022 - present:  Full-stack developer, Inetex LTD (Rehovot, Israel) \n" +
            "B2B Security with DDoS Analysis and Filtering: an access analyzer and requests blocker. Microservices architecture with one back-office service using Kafka for messaging, Spring Security for authentication handling, Redis for caching, Spring Boot for service implementation, PostgreSQL for storing data, and Spring Cloud for configuration, Docker and Kubernetes for deployment. Historical traffic data was collected and analyzed using AI to generate traffic behavior patterns. Implemented BDD methodology into the team’s development process, significantly enhancing test maintainability. Implemented a highly efficient caching strategy using Redis, leading to significantly faster data access.\n" +
            "Real-time Temperature Monitoring and Anomaly Detection System: using Spring Boot for microservices, Kafka for real-time data streaming, MongoDB for data, Docker for containerization and Kubernetes for orchestration. Conducted performance tuning and optimization of database queries, improving system response time. Designed and implemented RESTful APIs for communication between microservices, enhancing system flexibility and maintainability. Optimized sensor data collection intervals and processing algorithms, resulting in improved data processing speed.\n" +
            "Warehouse Management and Inventory Control System: implementing Spring Boot and Spring Cloud for microservices, Amazon Kinesis for streaming data, AWS Lambda for serverless computing, AWS Fargate for container orchestration, and AWS DynamoDB for scalable NoSQL data handling, enhanced with comprehensive RESTful APIs and integrated with AWS SNS for notifications. Implemented automated workflow processes to streamline warehouse operations, boosting operational efficiency. Adopted a microservices architecture to handle increased inventory and user loads, enhancing scalability. Optimized inventory turnover ratio through JIT (Just-In-Time) inventory management, reducing carrying costs. \n" +
            "Technologies: Spring Security, Kafka, Docker, Kubernetes, Spring Boot, Spring Data, Spring Cloud, Lombok, Unit tests, Mockito, Postman, Swagger; Kubernetes ConfigMap and secrets, MongoDB, PostgreSQL, AWS Systems Manager Parameter Store, AWS CloudWatch, AWS Fargate, AWS Lambda, Amazon SQS, Amazon RDS, Amazon DynamoDB, AWS CloudWatch.\n" +
            "2019 - 2022: Full-stack developer, WebCanape LTD (Moscow, Russia) \n" +
            "Online stores and websites: using JavaScript, React, Redux for frontend, Spring Boot for microservices architecture, Hibernate for database management, MySQL and MongoDB for data storage, API testing and validation with Postman. Increased application performance through optimized database queries and caching mechanisms. Reduced API response time by refactoring and optimizing the back-end code, leading to a more efficient and faster application. Enhanced customer satisfaction by introducing new features and improvements based on user feedback.\n" +
            "Technologies: HTML5, CSS, JavaScript, AJAX, jQuery, ReactJS, Redux, Bootstrap; Java, Spring Boot, Spring Data, Spring Cloud, Spring Security, Hibernate, MySQL, MongoDB, JUnit, Mockito, Postman.\n" +
            "Education\n" +
            "2004-2008 - Computer Science, Bachelor, Kuban Technological University (Krasnodar, Russia).\n" +
            "2000-2004 - Economy, Bachelor, Kuban Technological University (Krasnodar, Russia).\n" +
            "Languages: Hebrew (good), English (fluent), Russian (native)";
}
