[![CircleCI](https://circleci.com/gh/kusumotolab/kGenProg/tree/master.svg?style=svg)](https://circleci.com/gh/kusumotolab/kGenProg/tree/master)

# kGenProg
kGenProg は Java プログラム向けの自動プログラム修正ツールです．
C 言語向けの自動プログラム修正ツール GenProg の Java 向け実装です．
遺伝的アルゴリズムを用いて修正を行います．


## 動作条件
- JDK8


## インストール
kGenProg は単一の jar ファイルにまとめてあります．[ここ](https://github.com/kusumotolab/kGenProg/releases/download/v0.2/kGenProg.jar)から jar ファイルをダウンロードしてください．

[kusumotolab/kGenProg-example](https://github.com/kusumotolab/kGenProg-example) リポジトリに kGenProg の動作確認用のバグをまとめてあります．
[ここ](https://github.com/kusumotolab/kGenProg-example/archive/master.zip)からすべてのバグをまとめた zip ファイルをダウンロードできます．


## 使用方法

### 使用例
[kGenProg/example](example) には kGenProg のテストに用いているバグが置いてあります．
[kGenProg/example/CloseToZero01](example/CloseToZero01) に対して kGenProg を実行するには次のコマンドを実行してください．

```sh
$ cd kGenProg/example/CloseToZero01
$ java -jar path/to/kGenProg.jar -r ./ -s src/example/CloseToZero.java -t src/example/CloseToZeroTest.java
```


### オプション
| オプション | 説明 |
|---|---|
| `-r, --root-dir` | 修正対象プロジェクトのルートディレクトリへのパス．テスト実行の都合上，カレントディレクトリ以外のパスが指定された場合は警告が出力されます．（必須オプション） |
| `-s, --src` | プロダクトコード（単体テスト用のコードを除く実装系のソースコード）へのパス，もしくはプロダクトコードを含むディレクトリへのパス．スペース区切りで複数指定可能．（必須オプション） |
| `-t, --test` | テストコード（単体テスト用のソースコード）へのパス，もしくはテストコードを含むディレクトリへのパス．スペース区切りで複数指定可能．（必須オプション） |
| `-c, --cp` | 修正対象プロジェクトのビルドに必要なクラスパス．スペース区切りで複数指定可能． |
| `-w, --working-dir` | kGenProg が作業を行うディレクトリへのパス |
| `-o, --out-dir` | kGenProg が結果の出力を行うディレクトリへのパス |
| `-v, --verbose` | 詳細なログを出力する |
| `-q, --quiet` | エラー出力のみを行う |
| `--siblings-count` | 遺伝的アルゴリズムの変異操作によって1つの個体から1世代に生成する個体の数 |
| `--headcount` | 遺伝的アルゴリズムの選択操作によって1世代に残される個体の最大数 |
| `--max-generation` | 遺伝的アルゴリズムを打ち切る世代数 |
| `--time-limit` | 遺伝的アルゴリズムを打ち切る時間 |
| `--required-solutions` | 出力する解（修正パッチ）の数 |
| `--random-seed` | kGenProg 全体で用いる乱数のシード値 |
