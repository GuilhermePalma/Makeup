# 💪 Contribuindo com o Projeto

## Conteudos
- [Explicações](#-Explicações-sobre-o-Projeto)
- [Contribuindo com o Projeto](#-Contribuindo-com-o-Projeto)
    - [O que Contribuir ?](#-O-que-Contribuir-?)
    - [Como Contribuir ?](#-Como-Contribuir-?)
        - [Descomplicando o GIT](#-Descomplicando-o-GIT)
- [Erros e Bugs](#-Erros-e-Bugs)
- [Referencias e Guias](#*Referencias-e-Guias)

### Explicações sobre o Projeto
- Esse projeto utiliza o Padrão GitFlow. Isso permite uma maior segurança nos branches principais,
  dessa forma, protegendo o codigo-fonte de erros

- Caso utilize algum repositorio/site/artigo/recurso externo como apoio em alguma alteração, cite a
  fonte no arquivo [References](references.md)

- Caso tenha alguma duvida de como contribuir, inicie uma **Issue** utilizando as labels
  **question** e **contribute** e descreve sua duvida

- Acesse [Organização do Projeto](organization_project.md) para entender como o Projeto está organizado


### Contribuindo com o Projeto

Leia os Itens abaixo e entenda O que ? e Como ? você pode contribuir para esse repositorio

#### O que Contribuir ?
Acesse a aba [Project](https://github.com/GuilhermePalma/Makeup/projects) e entre na menor versão.
Nela terá lista "To Do", onde mostra todas as alterações que serão realizadas nessa versão. Escolha
um item que você queira contribuir.

Com um item Escolhido, entre na aba [Issue](https://github.com/GuilhermePalma/Makeup/issues) e
crie uma Issue com a label **contribute**. Descreva qual item e funcionalidades você irá desenvolver.

Outra forma de Contribuição está na aba [Issue](https://github.com/GuilhermePalma/Makeup/issues).
Caso você possa Contribuir em alguma Issue aberta, entre nela e escreve "Contribute: nome-do-user"


#### Como Contribuir ?

1. Acesse a Aba [Issues](https://github.com/GuilhermePalma/Makeup/issues) e escolha uma aberta ou
   acesse a Aba [Project](https://github.com/GuilhermePalma/Makeup/projects), entre na menor versão
   disponivel e escolha o Item que sera Desenvolvido
> Pule esse Passo caso seja uma alteração propria
2. Crie uma [Issue](https://github.com/GuilhermePalma/Makeup/issues), utilize a label
   **contribute** e descreva sobre qual Item e Funcionalidas você irá contribuir
3. Faça um **fork** do projeto
4. Clone seu repositorio no seu Computador e acesse a pasta criada
5. Entre no Branch 'develop', a partir dele sairá os branchs de alterações/correções/etc
6. Crie um novo seguindo o formato **prefixo/nome-da-alteração**
> Esse branch parte do **branch develop**
- Prefixos usados são:
- Feature: Para alterações
- Fix: Para Correções Simples
- HotFix: Para correções de extrema emergencia
- Refactor: Para mudanças na logica do codigo, grandes alterações ou substituição de metodos
7. Realize suas alterações e suba para o seu repositorio Fork
8. Inicie um Pull Request para o Repositorio Original
    - 7.1 - O Pull Request deve partir do seu branch com alterações (prefixo/nome-da-altercao) para
      o branch **develop** do repositorio original [Makeup - Guilherme Palma](https://github.com/GuilhermePalma/Makeup/tree/develop)
    - 7.2 - Na area da descrição, detalhe o que foi alterado/implementado/corrigido
    - 7.3 - (Opcional) Crie um titulo seguindo o modelo: **"Prefixo: Titulo do que foi Alterado"**

##### Descomplicando o GIT
```bash
# 4 - Faça um clone do seu repositorio FORK
git clone https://github.com/seu-usuario/Makeup.git

# 4 - Acesse a pasta do projeto no terminal/cmd
cd Makeup

# 5 - Acesse a Branch de Alterações - Develop
git checkout develop

# 6 - Crie uma branch (com o prefixo) com suas Alteracoes
git checkout -b prefixo/nome-da-altercao

# 6 - Sincronize a branch LOCAL com a REMOTA
git push origin prefixo/nome-da-altercao

# 7 - Realize suas Alteacoes e Adicione elas em um commit
git add .

# 7 - Salve as alteracoes com uma mensagem mostrando o que foi realizado
git commit -m "Feature: Minha Alteração no Codigo ...."

# 7 - Envie as Alteracoes para o seu Repositorio (Fork)
git push origin origin/prefixo/nome-da-altercao prefixo/nome-da-altercao

# 8 - Faça um Pull Request do seu BRANCH (prefixo/nome-da-altercao) para o meu BRANCH develop
git request-pull nome-pull-request https://github.com/GuilhermePalma/Makeup.git develop

```

### Erros e Bugs

Caso você encontre algum bug ou erro, entre na aba [Issue](https://github.com/GuilhermePalma/Makeup/issues)
e crie uma nova Issue. Nessa Issue, adicione a label **bug**, descreva o problema encontrado e caso
saiba de alguma forma de resolução, comente tambem.

### Referencias e Guias
- **[Guia Simplificado (Explicação Simples) de como Contribuir - PT-BR](https://github.com/firstcontributions/first-contributions/blob/master/translations/README.pt_br.md)**
- **[Documentação Git: Pull Request (Linha de Comando)](https://git-scm.com/docs/git-request-pull)**
- **[Documentação GitHub: Pull Request (Pelo Github)](https://docs.github.com/pt/github/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request)**