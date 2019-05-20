# 1. kGenProgとは？
遺伝的プログラミングを利用した自動プログラム修正ツールである．
kGenProgは入力としてバグのあるプログラムとテストケース群を受け取る．
つまり，入力として与えられたプログラムは，同じく入力として与えられた少なくとも1つのテストケースに失敗する．
kGenProgは与えられたバグのあるプログラムを，変異と交叉という二種類の変更を加えていくことにより，全てのテストケースを成功するプログラムへと変換する．
以降，kGenProgで利用されている用語，kGenProgのアーキテクチャ等を説明する．

# 2. 用語

kGenProgでは遺伝的プログラミングを利用しているため，kGenProgでは遺伝的プログラミングに由来する多くの用語が使われている．
ここでは，それらの用語について説明する．

## 個体

個体とはプログラムを表す用語である．
変異や交叉が行われた個体は，それが行われる前の個体とは別の個体と見なされる．

## 自動バグ限局

kGenProgは，全てのテストケースを成功する個体を生み出すために，与えられた個体に対して変更を加える．
変更を加える箇所を決定するために，kGenProgは自動バグ限局という処理を入力された個体に対して行う．
kGenProgにおける自動バグ限局とは，与えられたプログラムと各テストケースの実行結果（テストの成否，およびプログラムのどの文が実行されたのか）の情報を利用して，バグの原因になっていそうな箇所を絞り込む処理である．
具体的には，対象プログラムの全ての文に疑惑値という0～1の数値が割り振られる．
疑惑値が0よりも大きい場合，その文はバグの原因箇所である可能性があり，疑惑値が1に近いほどその可能性が高いことを表す．


## 変異

変異とは，既存の個体（プログラム）から次の世代の個体を生み出す処理である，
変異は，挿入，削除，および置換の3つの操作からなる．
- 挿入：バグ限局された箇所（行）の次の行に，プログラム要素を挿入する操作である．
- 削除：バグ限局された箇所（行）を削除する操作である．
- 置換：バグ限局された箇所（行）を，プログラム要素で置換する操作である．

上記の処理において，プログラム要素は，単文もしくは複文である．
単文とは，変数宣言文やreturn文のような単一のプログラム文を表す．
複文とは，if文やfor文のように，その要素の中に他の文を含みうるプログラム文を表す．


## 交叉

交叉も変異と同じく，既存の個体（プログラム）から次の世代の個体を生み出す処理である．
変異と交叉の違いは，変異が1つの個体から次世代の個体を生み出すのに対して，交叉は複数の個体から次世代の個体を生み出す処理である．
もう一つ変異と交叉との違いは，変異では新しい操作を適用して個体を生成するのに対して，変異ではこれまでに個体に適用された操作を再利用して新しい個体を生成する．

## 塩基と遺伝子

kGenProgにおける塩基とは，挿入，削除，もしくは置換のいずれかの操作を表す．
そして遺伝子とは塩基の列を表す．
入力として与えられた個体（プログラム）に，遺伝子に含まれる全ての塩基（操作）を適用することによって，その個体のソースコードを生成することができる．
kGenProgでは，実行中に生成される個体はその遺伝子により表現される．
つまり，入力として与えられたプログラムは長さが0の遺伝子を持ち，生成された各個体は，その個体を生成するために必要な操作の数を長さとして持つ遺伝子からなる．


# 3. アーキテクチャ

遺伝的プログラミングを用いた自動プログラム修正は，その処理を行う上でさまざまな選択肢がある．
例えば，kGenProgは自動バグ限局手法としてAmple，Jaccard，Ochiai，Tarantula，Zoltarが実装されている．
また，これら以外の自動バグ限局手法も詳細実装されるかもしれないし，kGenProgのユーザがこれら以外の手法を利用したい場合もあるだろう．
このようなことから，kGenProgでは，自動プログラム修正過程の種々の処理をインターフェース化した実装を行っている．
よって，新しい自動バグ限局アルゴリズムを実装した場合でも，既存のkGenProgコードの書き換えは最小限の量で済む．

以降，本質では，各処理のインターフェースと実装クラスについて述べる．


## 自動バグ限局のインターフェース FaultLocalization

自動バグ限局のインターフェースはjp.kusumotolab.kgenprog.fl.FaultLozalizationである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.fl.Ample
- jp.kusumotolab.kgenprog.fl.Jaccard
- jp.kusumotolab.kgenprog.fl.Ochiai
- jp.kusumotolab.kgenprog.fl.Tarantula
- jp.kusumotolab.kgenprog.fl.Zoltar
いくつかの論文では，Ochiaiが最も性能が高いと報告している．
KGenProgのデフォルト値はOchiaiである．


## 変異のインターフェース Mutation

変異のインターフェースはjp.kusumotolab.kgenprog.ga.mutation.Mutationである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.mutation.RandomMutation：変異により変更する箇所，変異の操作，操作が挿入と置換の場合に利用するプログラム文の取得を，ランダム選択により決定するクラスである．


## 交叉のインターフェース Crossover

交叉のインターフェースはjp.kusumotolab.kgenprog.ga.crossover.Crossoverである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.crossover.RandomCrossover：二つの親個体から全ての塩基を取得後，それらのうちの半分をランダムに選択して新しい個体を生み出す交叉である．
- jp.kusumotolab.kgenprog.ga.crossover.SinglePointCrossover：二つの親個体の遺伝子をある一点で前後に分割し，親個体Aの前半遺伝子と親個体Bの後半遺伝子から新しい個体を生み出す較差である．
- jp.kusumotolab.kgenprog.ga.crossover.UniformCrossover：二つの親個体の遺伝子を並べ，同じ位置にある塩基のどちらを用いるかをランダム選択する．
つまり，親個体Aの位置0の塩基と親個体Bの位置0の塩基のどちらかをランダム選択し，親個体Aの位置1の塩基と親個体Bの位置1の塩基のどちらかをランダム選択する，という処理を遺伝子の長さの回数行う．


## 交叉の第一の親を選択するためのインターフェース FirstVariantSelectionStrategy

交叉の第一の親を選択するためのインターフェースはjp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategyである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.crossover.FirstVariantEliteSelection：第一の親として適応度が高い個体を選ぶ戦略である．
- jp.kusumotolab.kgenprog.ga.crossover.FirstVariantRandomSelection：第一の親としてランダムに個体を選ぶ戦略である．


## 交叉の第二の親を選択するためのインターフェースSecondVariantSelectionStrategy

交叉の第二の親を選択するためのインターフェースはjp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategyである．
現在のところ，実装クラスとしては以下のものがある．
なお，全ての実装クラスでは，第一の親と第二の親として同じ個体を選ばないように実装されている．
- jp.kusumotolab.kgenprog.ga.crossover.SecondVariantEliteSelection：第二の親として適応度が高い個体を選ぶ戦略である．
- jp.kusumotolab.kgenprog.ga.crossover.SecondVariantGeneSimilaritySelection：第一の親とできるだけ異なる遺伝子を持つ個体を第二の親として選ぶ戦略である．個体の遺伝子はJaccard係数を利用して計算される．
- jp.kusumotolab.kgenprog.ga.crossover.SecondVariantRandomSelection：第二の親としてランダムに個体を選ぶ戦略である．
- jp.kusumotolab.kgenprog.ga.crossover.SecondVariantTestComplementarySelection：第一の親が失敗しているテストケースをできるだけ成功している個体を第二の親として選ぶ戦略である．


## 次の世代の個体を生成する基となる個体を選択するためのインターフェース VariantSelection

個体選択のインターフェースはjp.kusumotolab.kgenprog.ga.selection.VariantSelectionである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection：適応度が高い個体を選択する．適応度が等しい個体が複数存在する場合はランダムに選択される．
- jp.kusumotolab.kgenprog.ga.selection.EliteAndOldVariantSelection：適応度が高い個体を選択する．適応度が等しい個体が複数存在する場合は古い世代で生成された個体が選択される．
- jp.kusumotolab.kgenprog.ga.selection.GenerationalVariantSelection：DefaultVariantSelectionとの違いがいまいち不明


## 個体の適応度を計算するためのインターフェース SourceCodeValidation
適用度を計算するためのインターフェースはjp.kusumotolab.kgenprog.ga.validation.SourceCodeValidationである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation：テストの実行結果をもとに適用度を計算する．


## 適用度を表すインターフェース Fitness
適用度を表すインターフェースはjp.kusumotolab.kgenprog.ga.validation.Fitnessである．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.ga.validation.SimpleFitness：0から1までの数値として適用度を表現するクラスである．


## 抽象構文木の変更操作を表すインターフェース JDTOperation
抽象構文木の変更操作を表すインターフェースは jp.kusumotolab.kgenprog.project.jdt.JDTOperation である．
現在のところ，実装クラスとしては以下のものがある．
- jp.kusumotolab.kgenprog.project.jdt.DeleteOperation：プログラム文の削除を表す操作
- jp.kusumotolab.kgenprog.project.jdt.InsertOperation：プログラム文の追加を表す操作
- jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation：プログラム文の置換を表す操作







