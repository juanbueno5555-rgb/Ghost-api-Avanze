---
title: Diagrama de Despliegue - CI/CD Tech Cup
---

Dev [color: #1E3A5F] {
  IDE [icon: code]
  Docker [icon: cube]
  Maven [color: #60A5FA]
}

GitHubActions [color: #422006] {
  Build [color: #F59E0B]
  Test [color: #F59E0B]
  JaCoCo [color: #F59E0B]
  SlackNotify [color: #10B981]
  AutoIssue [color: #EF4444]
}

QA [color: #064E3B] {
  SpringBoot [color: #10B981]
  PostgreSQLQA [icon: database] [color: #10B981]
}

PROD [color: #1A032E] {
  SpringBootPROD [color: #7C3AED]
  PostgreSQLPROD [icon: database] [color: #7C3AED]
}

Dev > GitHubActions: "git push"
GitHubActions > QA: "Deploy si tests pasan"
QA > PROD: "Deploy si validacion OK"
