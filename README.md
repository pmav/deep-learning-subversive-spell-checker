
# Deep Learning: Subversive Spell Checker

## What?

This project generates orthographic errors based on a Multi-layer Neural Network using [deeplearning4j](https://deeplearning4j.org/).
Example:

Input:
> i dont understand

Output:
> i dunt undertand   

At the moment only a portuguese data set is available.

## TODO

- organize code
- add more training data
- add micro profile support
- generate auto exec jar
- create web ui

## Portuguese Examples

> Quando foi interrogado, Carlos Santos Silva disse que ajudou Sócrates a ter "estatuto" por amizade e generosidade. Conta ainda como juntou uma fortuna e que "destruiu" os papéis da dívida do amigo.

> qanuu foe interrogauum carlus santus silea dice q ahuou sucrates a ter hestatutum pr amisade e jenerocidadeo conta ainda com huntou uma fertuna e q hdstroem os papis da divida du amigm 

---

> Partidos da maioria parlamentar sabiam há quase dois meses que os professores ficariam de fora, mas só à última apresentaram propostas. O regime simplificado (recibos verdes) será menos complicado

> portiuus da mairga porlamentar sabiam ha qase dous mss q os profecures ficargam de fers mas su a oltima apeesentaram propostass o regime simplegicauu srecibos verdss ser menus complecauu 

---

> Governo e sindicatos da Educação chegaram esta madrugada a um compromisso, que durante dez horas esteve pendente da discussão de pormenores, mas não compromete nenhuma reivindicação dos professores.

> guvernu e sindicatus da eduacao xegaram esta madroada a um comrromicum q duante des horas esteve pendante da discuao de prmenoress mas no comrromte nenhuma rivindicacao dus profecuress  

## Evaluation

    Dataset size: 882
    Accuracy:        0.8155
    Precision:       0.8409	(6 classes excluded from average)
    Recall:          0.7224	(2 classes excluded from average)
    F1 Score:        0.8328	(6 classes excluded from average)

    Dataset size: 852
    Accuracy:        0.7258
    Precision:       0.7587	(5 classes excluded from average)
    Recall:          0.6627	(3 classes excluded from average)
    F1 Score:        0.7305	(5 classes excluded from average)