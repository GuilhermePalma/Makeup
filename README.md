<h1 align="center">💄 API - Maquiagem</h1>


<h3 align="center">
    💄 Busca de Maquiagens dentro da <a href="http://makeup-api.herokuapp.com" tagert="_blank">API - Makeup</a>
</h3>

<p align="center">
  <img alt="GitHub language count" src="https://img.shields.io/github/languages/count/GuilhermeCallegari/Maquiagem?color=2304D361">

  <img alt="Repository size" src="https://img.shields.io/github/repo-size/GuilhermeCallegari/Maquiagem">
  
  <a href="https://github.com/GuilhermeCallegari/Maquiagem/commits/main">
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/GuilhermeCallegari/Maquiagem">
  </a>
    
   <img alt="License" src="https://img.shields.io/github/license/GuilhermeCallegari/Maquiagem">
</p>

<h4 align="center">
	🚧   Em Construção 🚀 🚧
</h4>

Tabela de conteúdos
=================
<!--ts-->
 * [Sobre o projeto](#-sobre-o-projeto)
   * [Funcionalidades](#-funcionalidades)
   * [Layout](#-layout)
     * [Mobile](#mobile)
   * [Como executar o projeto](#-como-executar-o-projeto)
     * [Pré-requisitos](#pré-requisitos)
   * [Tecnologias](#-tecnologias)
   * [Contribuidores](#-contribuidores)
   * [Como contribuir no projeto](#-como-contribuir-no-projeto)
<!--te-->


# 💻 Sobre o projeto

💄 Makeup - é um aplicativo que busca em uma API dados sobre maquiagem, listando Nome, Preço, Tipo, Marca e Descrição de cada Produto


O Projeto consumiu a API Makeup, para ver sua documentação, acesse [API Makeup](http://makeup-api.herokuapp.com/)

---


## ⚙️ Funcionalidades

- [x] Os usuarios podem pesquisar na API usando os filtros de:
  - [x] Marca
  - [x] Tipo

- [x] A Aplicação ira exibir:
  - Imagem
  - Nome
  - Preço
  - Marca
  - Tipo
  - Descrição

---


## 🎨 Layout

O layout da aplicação está disponível no Whimsical:

<a href="https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm">
  <img alt="Made by Guilherme Palma e Guilherme Callegari" src="https://img.shields.io/badge/Acessar%20Layout-Whimsical-green">
</a>

### Mobile

<p align="center">
  <img alt="NextLevelWeek" title="#NextLevelWeek" src="./assets/home-mobile.png" width="200px">

  <img alt="NextLevelWeek" title="#NextLevelWeek" src="./assets/detalhes-mobile.svg" width="200px">
</p>


## 🚀 Como executar o projeto

Este projeto é divido em três partes:
1. [Layout](app/src/main/res/layout/) (Parte Grafica)
2. [Funções do Sistema](app/src/main/java/com/example/maquiagem/)
3. [Classe](app/src/main/java/com/example/maquiagem/Model/)



### Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
[Git](https://git-scm.com) e um editor para trabalhar com o código como [Android Studio](https://developer.android.com/studio/)


#### 📥 Baixando o Projeto

```bash

# Clone este repositório
$ git clone https://github.com/GuilhermeCallegari/Maquiagem.git

# Acesse a pasta do projeto no terminal/cmd
$ cd Maquiagem

```


---


## 🛠 Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:


#### **Mobile** 

-   **[Java](https://developer.android.com/docs)**


#### **Utilitários**

-   Protótipo:  **[Winsic](https://whimsical.com/)**  →  **[Protótipo (Maquiagem)](https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm)**
-   API:  **[API Makeup](http://makeup-api.herokuapp.com/)**  →  **[API de Tipos](makeup-api.herokuapp.com/api/v1/products.json?product_type=)**,  **[API de Marcas](http://makeup-api.herokuapp.com/api/v1/products.json?brand=)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extensions:  **[SQLite](https://developer.android.com/training/data-storage/sqlite?hl=pt-br)**
-   Teste de API:  **[Swagger](https://editor.swagger.io/)**


---


## 👨‍💻 Contribuidores

💜 Desenvolvedores que contruiram o Aplicativo :)

<table>
  <tr>
    <td align="center"><a href="https://github.com/GuilhermeCallegari"><img style="border-radius: 50%;" src="https://avatars.githubusercontent.com/u/66626306?s=400&v=4" width="100px;" alt=""/><br /><sub><b>Guilherme Callegari</b></sub></a><br /><a href="https://github.com/GuilhermeCallegari" title="GitHub">🚀</a></td>
    <td align="center"><a href="https://github.com/guilhermepalma"><img style="border-radius: 50%;" src="https://avatars.githubusercontent.com/u/54846154?s=60&v=4" width="100px;" alt=""/><br /><sub><b>Guilherme Palma</b></sub></a><br /><a href="https://github.com/guilhermepalma" title="Github">🚀</a></td>
  </tr>
</table>


## 💪 Como contribuir no projeto

1. Faça um **fork** do projeto.
2. Crie uma nova branch com as suas alterações: `git checkout -b my-feature`
3. Salve as alterações e crie uma mensagem de commit contando o que você fez: `git commit -m "feature: My new feature"`
4. Envie as suas alterações: `git push origin my-feature`


---
