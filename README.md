# 🥷 **AI Github Issues Analyzer – Feed Gemini your open issues and ask anything!**

## You can directly use the deployed version here: [api.github.cursedshrine.co.in](https://api.github.cursedshrine.co.in)

# Implementation Choice:

- **For Backend I am using spring boot with java 25 (latest and greatest)**
- **for storing github issues I am using in memory hashmap because there’s no point in storing the issues twice if it’s already available via api.**
- **requests are validated and will throw exceptions (bad request on api response)**

## Installation (Via Docker):

### Step 1: application.yml (environment variables):

populate the application.yml file located in:

```jsx
src/main/resources/application.yml
```

```jsx
server:
  port: 7771

spring:
  servlet:
    multipart:
      max-file-size: 51MB
      max-request-size: 51MB
  application:
    name: intelli-issue

google:
  gemini:
    api-key: YOUR_API_KEY
```

### Step 2: Build the docker image:

Open the terminal inside the repository run this command:

```jsx
docker build -t intelli-issue .
```

### Step 3: run the docker container:

Now we can run the container and expose its port (7771) on the host machine. Open a terminal anywhere and run this command:

You can also change the host port number like this -p 3000:7771 (docker port must remain 7771)

**Now your GitHub issue is live on your localhost.**

```jsx
docker run -d --name intelli-issue-container -p 7771:7771 intelli-issue
```

## Usage:

### scan your repositories for issues: (owner/repo-name)

```jsx
curl --location 'localhost:7771/api/issue/scan' \
--header 'Content-Type: application/json' \
--data '{

  "repo": "sagarkpro/webhook-tester"

}'
```

### Run analysis on the scanned issues:

```jsx
curl --location 'localhost:7771/api/issue/analyze' \
--header 'Content-Type: application/json' \
--data '{
    "repo": "sagarkpro/webhook-tester",
    "prompt": "Find themes across recent issues and recommend what the maintainers should fix first"
}'
```