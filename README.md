# CodeSage: AI-Powered Development Assistant

CodeSage is a full-stack, AI-powered development tool designed to analyze, document, and enhance your code. Built on the Spring Framework with Spring AI, it leverages a local Large Language Model (LLM) via Ollama to function as an intelligent and private development partner, helping you improve code quality and accelerate your workflow.
✨ Key Features
Comprehensive Code Analysis: Upload an entire project and receive an AI-generated Code Review that identifies bugs and smells, along with high-quality Documentation (README.md) summarizing the project's purpose.
Interactive Project Explorer: A web-based UI that displays the full directory structure of your project. Click on any file to view its content with syntax highlighting.
Automated Test Generation: Select any Java class in the Project Explorer and have the AI automatically generate a complete JUnit 5 test class, including mocks where necessary.
AI-Powered Code Refactoring: Highlight any snippet of code in the viewer and ask the AI to refactor it based on goals like "improve readability," "improve performance," or "add comments."
Guided Implementation: Describe a new feature or a change in plain English, and the AI will generate a detailed, step-by-step Markdown guide with code snippets for you to follow.
Database Connection Analysis: The tool automatically scans configuration files (e.g., application.properties) to detect and display the project's database connection details.
Semantic Understanding: On analysis, the application generates and stores vector embeddings for each file in a pgvector-enabled PostgreSQL database, laying the groundwork for future semantic search capabilities.
🛠️ Technology Stack
Category
Technology
Backend
Java, Spring Boot, Spring AI, Maven
AI Model
Local LLM via Ollama (e.g., Codestral, StarCoder2, Phi-3)
Database
PostgreSQL with the pgvector extension
Frontend
Single-page application using vanilla HTML, CSS, and JavaScript
🚀 Getting Started
Follow these steps to set up and run the CodeSage project on your local machine.
Prerequisites
Java Development Kit (JDK): Version 17 or newer.
Apache Maven: For building the project.
PostgreSQL: Version 14 or newer.
Ollama: To run the local LLM. Make sure your machine has a compatible GPU for the best performance.

1. Database Setup
You must have PostgreSQL installed and running with the pgvector extension enabled.
Install pgvector: Follow the official installation guide for your operating system: [pgvector GitHub](https://github.com/pgvector/pgvector).
Create the Database and Enable the Extension:
-- Connect to PostgreSQL
psql

-- Create a database for the project
CREATE DATABASE codesagepro;

-- Connect to the new database
\c codesagepro

-- Enable the vector extension
CREATE EXTENSION vector;

2. Ollama Setup
Install Ollama: Download and install Ollama from the [official website](https://ollama.com/).
Pull an AI Model: Download a code-specialized model. It is highly recommended to use a small, efficient model for better performance.

# We recommend starting with Phi-3

ollama pull phi3

(Optional but Recommended) Create a Custom Model for a Larger Context Window: To avoid errors with large projects, create a custom model.
Create a file named Modelfile with the following content:
FROM phi3
PARAMETER num_ctx 16384
PARAMETER num_gpu 999

Run the create command in your terminal:
ollama create codesage-phi3 -f Modelfile

3. Application Configuration
Clone the Repository:
git clone [https://github.com/your-username/codesage.git](https://github.com/your-username/codesage.git)
cd codesage

Configure the Application:
Open the src/main/resources/application.properties file.
Update the database connection and AI model properties to match your setup.

# Database Connection

spring.datasource.url=jdbc:postgresql://localhost:5432/codesagepro
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update

# Spring AI and Ollama Configuration

spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=codesage-phi3 \# Use your custom model name
spring.ai.ollama.embedding.options.model=codesage-phi3 \# Use your custom model name

4. Run the Application
Build the Project:
mvn clean package

Run the JAR file:
java -jar target/Code-Sage-Pro-0.0.1-SNAPSHOT.jar

The application will start on http://localhost:8080.
📖 Usage
Open your web browser and navigate to http://localhost:8080.
Click the "Choose File" button and select a .zip file of a software project you want to analyze.
Click the "Analyze Project" button.
After a few moments, the Project Explorer, Database Analysis, and AI Analysis Results will appear.
Explore Files: Click on any file in the Project Explorer to view its content.
Generate Tests: Click on a .java file, and a green "Generate Tests" button will appear. Click it to get AI-generated JUnit tests in a pop-up.
Refactor Code: Highlight any piece of code in the viewer to bring up the Refactor Toolbar. Choose a goal and click "Refactor" to see the AI's suggestion.
Get Implementation Guidance: Use the "Guided Implementation" section to describe a new feature and receive a step-by-step guide from the AI.

now give me this file in ReadMe.md form

Here’s your project description transformed into a well-structured `README.md`:

***

# CodeSage: AI-Powered Development Assistant

CodeSage is a **full-stack, AI-powered development assistant** that analyzes, documents, and enhances your code. Built on the **Spring Framework with Spring AI**, it leverages a **local Large Language Model (LLM) via Ollama** to act as an intelligent and private development partner, helping you improve code quality and accelerate your workflow.

***

## ✨ Key Features

- **Comprehensive Code Analysis**: Upload an entire project and get AI-generated *Code Reviews* with bug detection, code smells, and auto-documentation (`README.md`).
- **Interactive Project Explorer**: Browse your project’s directory structure in a web UI with syntax highlighting.
- **Automated Test Generation**: Generate complete JUnit 5 test classes for any Java file, including mocks.
- **AI-Powered Code Refactoring**: Highlight snippets and refactor using AI with goals like *improve readability, performance, or add comments*.
- **Guided Implementation**: Describe a new feature in plain English, and get an AI-generated step-by-step implementation guide.
- **Database Connection Analysis**: Automatically detect database connection details from configuration files.
- **Semantic Understanding**: Stores vector embeddings for each file into **pgvector-enabled PostgreSQL**, enabling future semantic code search.

***

## 🛠️ Technology Stack

| Category | Technology |
| :-- | :-- |
| Backend | Java, Spring Boot, Spring AI, Maven |
| AI Model | Local LLM via Ollama (e.g., Codestral, StarCoder2, Phi-3) |
| Database | PostgreSQL with pgvector extension |
| Frontend | Vanilla HTML, CSS, JavaScript |


***

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Apache Maven**
- **PostgreSQL 14+** (with `pgvector` extension enabled)
- **Ollama** (GPU recommended for best performance)

***

### 1. Database Setup

Enable `pgvector` in your PostgreSQL database:

```sql
-- Connect to PostgreSQL
psql

-- Create a new project database
CREATE DATABASE codesagepro;

-- Connect to the database
\c codesagepro

-- Enable pgvector extension
CREATE EXTENSION vector;
```


***

### 2. Ollama Setup

1. Install Ollama from the [official website](https://ollama.com/).
2. Pull a code-specialized model:
```bash
ollama pull phi3
```

3. *(Optional, recommended)* Create a custom model with a larger context window:

Create a `Modelfile`:

```txt
FROM phi3
PARAMETER num_ctx 16384
PARAMETER num_gpu 999
```

Build the model:

```bash
ollama create codesage-phi3 -f Modelfile
```


***

### 3. Application Configuration

Clone the repository:

```bash
git clone https://github.com/your-username/codesage.git
cd codesage
```

Configure the application in `src/main/resources/application.properties`:

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/codesagepro
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update

# Spring AI + Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=codesage-phi3
spring.ai.ollama.embedding.options.model=codesage-phi3
```


***

### 4. Run the Application

Build the project:

```bash
mvn clean package
```

Start the application:

```bash
java -jar target/Code-Sage-Pro-0.0.1-SNAPSHOT.jar
```

The app will be available at:

```
http://localhost:8080
```


***

## 📖 Usage

1. Open [http://localhost:8080](http://localhost:8080).
2. Upload a `.zip` file of a project.
3. Click **Analyze Project** to start.
4. Explore features:
    - **Project Explorer**: Navigate file structure and view contents.
    - **Generate Tests**: Auto-generate JUnit 5 tests for any `.java` file.
    - **Refactor Code**: Select code snippets, choose a refactoring goal, and apply AI suggestions.
    - **Guided Implementation**: Request a new feature in plain English, and AI will generate a step-by-step implementation plan.
    - **Database Analysis**: View detected database configuration details.

