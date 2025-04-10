# Software Engineering Quiz Application

A Java Swing-based desktop application that generates Software Engineering quiz questions using AI. The questions are dynamically generated using OpenAI's GPT-3.5 model, ensuring a unique learning experience each time.

## Features

- Dynamic AI-generated questions about software engineering
- Beautiful modern UI with gradient background
- Multiple choice questions with immediate feedback
- Score tracking
- Responsive design

## Prerequisites

- Java 11 or higher
- Maven
- OpenAI API Key

## Setup

1. Clone this repository
2. Make sure you have Java 11+ and Maven installed
3. Get an OpenAI API key from [OpenAI's website](https://platform.openai.com)
4. Build the project:
   ```bash
   mvn clean package
   ```

## Running the Application

Run the application using:
```bash
java -jar target/software-engineering-quiz-1.0-SNAPSHOT.jar
```

When the application starts, you'll be prompted to enter your OpenAI API key. The key is required to generate questions.

## How to Play

1. Read the question carefully
2. Select one of the four options
3. Click "Submit Answer" to check if your answer is correct
4. Click "Next Question" to proceed to the next question
5. Your score is tracked at the bottom of the window

## Note

The application requires an active internet connection to generate questions using the OpenAI API. 