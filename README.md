<h1 align="center" id="title">💄 API - Maquiagem</h1>


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

<p align="center">
  <img alt="Imagem da Tela Principal" title="Inicio" src="./printscreen/LightMode/Index.jpeg" width="200px">
	
  <img alt="Imagem do Menu de Opções" title="Menu" src="./printscreen/LightMode/MenuOptions.jpeg" width="200px">
	
  <img alt="Imagem da Tela com Dados de Pesquisa" title="CustomView" src="./printscreen/LightMode/CustomView.jpeg" width="200px">
	
  <img alt="Imagem da Tela de Resultados" title="ResultadosAPI" src="./printscreen/LightMode/ResultApi.jpeg" width="200px">
	
  <img alt="Imagem da Tela Localização Atual" title="Localizacao" src="./printscreen/LightMode/Location.jpeg" width="200px">
	
  <img alt="Imagem do Feedback da Localização" title="FeedbackLocalizacao" src="./printscreen/LightMode/LocationWithFragment.jpeg" width="200px">
	
  <img alt="Imagem do Sensor de Proximidade Longe" title="SensorLonge" src="./printscreen/LightMode/SensorProximity.jpeg" width="200px">
	
  <img alt="Imagem do Sensor de Proximidade Perto" title="SensorPerto" src="./printscreen/LightMode/SensorProximityOff.jpeg" width="200px">
</p>



Tabela de conteúdos
=================
<!--ts-->
 * [Sobre o projeto](#-sobre-o-projeto)
   * [Funcionalidades](#funcionalidades)
   * [Layout](#-layout)
     * [Mobile](#mobile)
   * [Como executar o projeto](#-como-executar-o-projeto)
     * [Pré-requisitos](#pré-requisitos)
   * [Tecnologias](#-tecnologias)
   * [Contribuidores](#-contribuidores)
   * [Como contribuir no projeto](#-como-contribuir-no-projeto)
<!--te-->


# 💻 Sobre o projeto

💄 **Makeup** - Projeto desenvolvido durante a materia Programação e Algoritimos Mobile II - ETEC 2021, coordenado pela Professora [Aline F. Brito](https://github.com/alinefbrito). 

:books: Esse aplicativo busca dados em uma API de Maquiagem, Listando Imagem, Nome, Preço, Tipo, Marca e Descrição de cada Produto

O Projeto utilizou a API Makeup. Para ver sua documentação, acesse [API Makeup](http://makeup-api.herokuapp.com/)

---


## Funcionalidades

- [x] Os usuarios podem pesquisar na API usando os filtros de:
  - [x] Marca
  - [x] Tipo

- [x] A Aplicação ira exibir:
  - [X] **Imagem** 
  - [x] Nome
  - [x] Preço
  - [x] Marca
  - [x] Tipo
  - [x] Descrição

---


## 🎨 Layout

O Layout da Aplicação está Disponível no [Whimsical](https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm):

<a href="https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm">
  <img alt="Made by Guilherme Palma e Guilherme Callegari" src="https://img.shields.io/badge/Acessar%20Layout-Whimsical-green">
</a>

### Mobile

**Imagens** das Telas do APP - [Tema Normal](printscreen/LightMode) e [Tema Escuro](printscreen/DarkMode)

**Video** Mostrando o Funcionamento do APP - [API - Makeup](./app/src/main/res/drawable/Makeup%20-%20API.mp4)  
    

## 🚀 Como executar o projeto

Este projeto é divido em três partes:
1. [Layout das Telas](app/src/main/res/layout/)
2. [Configurações das Telas](app/src/main/java/com/example/maquiagem/view/activity)
3. [Elementos das Telas](app/src/main/java/com/example/maquiagem/view)
4. [Funções do Sistema](app/src/main/java/com/example/maquiagem/model)
5. [Layout Widget](app/src/main/res/layout/widget_app.xml)
6. [Configurações do Widget](app/src/main/java/com/example/maquiagem/view/WidgetApp.java)



### Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
- [Git](https://git-scm.com) → Atualizações e Versionamento no Codigo 
- [Android Studio](https://developer.android.com/studio/) → Editor da Google voltado ao Desenvolvimento Android


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
-   **[Java](https://developer.android.com/docs)**


#### **Utilitários**

-   Protótipo:  **[Winsical](https://whimsical.com/)**  →  **[Protótipo (Maquiagem)](https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm)**
-   API:  **[API Makeup](http://makeup-api.herokuapp.com/)**  →  **[API de Tipos](makeup-api.herokuapp.com/api/v1/products.json?product_type=)**,  **[API de Marcas](http://makeup-api.herokuapp.com/api/v1/products.json?brand=)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[ToolBar](https://developer.android.com/reference/android/widget/Toolbar)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[Tarefas Assincronas](https://developer.android.com/reference/android/os/AsyncTask)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[SQLite](https://developer.android.com/training/data-storage/sqlite?hl=pt-br)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[Localização](https://developer.android.com/training/location/retrieve-current?hl=pt-br)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[Sensores](https://developer.android.com/guide/topics/sensors/sensors_overview?hl=pt-br)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[Widget](https://developer.android.com/guide/topics/appwidgets/overview)**
-   Editor:  **[Android Studio](https://developer.android.com/studio/)**  → Extenção:  **[AlertDialog](https://developer.android.com/guide/topics/ui/dialogs?hl=pt-br)**
-   Teste de API:  **[Swagger](https://editor.swagger.io/)**
-   Biblioteca Picasso:  **[Documentação Picasso](https://square.github.io/picasso/)** 
	> Conversão de URLs para Imagens
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
