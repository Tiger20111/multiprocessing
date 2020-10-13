###### **Замена await, signal, signalAll на wait, notify, notifyALL**

Рассмотрим для начала, что такое Condition, чтобы понять, как заменять.

Condition - объектные методы монитора, который в сочетании с блокировкой Lock позволяет заменить методы монитора/мьютекса (wait, notify и notifyAll).

1) Блокировка Lock Заменяет использование synchronized

2) await - Переводит поток в состояние ожидания на определенное время пока не будет выполнено некоторое условие или пока другой поток не вызовет методы signal/signalAll

3) signal - Сигнализирует потоку, у которого ранее был вызван метод await(), о возможности продолжения работы. Применение аналогично использованию методу notify класса Object

4) signalAll() - Сигнализирует всем потокам, у которых ранее был вызван метод await(), о возможности продолжения работы. Применение аналогично использованию методу notifyAll класса Object

###### Проведем аналогию того, что на что заменяем, а потом покажем корректность.

**Вместо того, чтобы использовать Lock lock, будем использовать Object lock.** 

a) В следствии чего 1. означает то же, что и метод wait класса Object по отношению notify/notifyAll.

Он так же проснется по их вызову.

А значит соотвествующие части мы можем заменить. 

b) Остается разобрать lock.lock()

Заменим кострукцию на synchronized(lock).
 
 Так как до этого вначале метода, что ReadLock.lock(), что ReadLock.unlock() начинается с того, что мы захватываем lock, то в данную секцию может зайти только один поток.

Означает, что если мы заменим на synchronized(lock) для Object, то это свойство сохранится. Обернутый блок кода будет выполняться только одним потоком.

По сути мы захватили встроенный мьютекс в классе Object.

c) Во время метода await() может произойти исключение, и тогда нам самим нужно освободить ресурсы, для этого стоит обертку try catch, такую же обертку переносим, потому, что для метода wait класса Object возможность исключения сохраняется. Только благодаря полю synchronized unlock писать не нужно. 

Так как выброс исключения конкретный, то ошибку можем напечатать на экран. В случаях с notify и notifyAll таких ошибок нет.

Мы провели замену 1 к 1 всего, что означает, если код до этого работал с Condition, то должен работать и с Монитором

###### **Корректность работы:**
Поля int readers и  boolean writer можно не делать volatile просто потому, что доступ к ним ведется только из блоков synchronized, в котором может находиться только один поток. И полу synchronized обеспечивает happens-before. Но поля делаем private, чтобы вне нашего класса его нельзя было изменить.

Так как мы хотим, чтобы все потоки со чтением спали, спали пока кто-то пишет, то они так же приходят в метод Lock и видя, что кто-то пишет спят, иначе они присоединяются к читающим.

Если же поток уходит с чтения и он был не один, то он просто сбрасывает себя со счетчика читателей и существующий мир остается прежним.

В случае же, если это был последний, то он так же пошлет всем спящим потокам просыпаться. А дальше, уже зависит от того, какой поток успеет проснуться первым - читающий или пищущий.

Если же в данный момент кто-то пишет, то все остальные потоки по прежнему видя это засыпают.

Ну и если перестал писать, то достаточно снять табличку writer, изменение которой happens-before, для всех и послать всем сигнал проснуться.

Объект Object можно было бы сделать final, чтобы никакой другой поток не изменил ссылку на него и два потока не зашли в одну секцию, но можно сделать его private и проследить за тем, что ни один из наших реализуемых методов к этому не приводит. 

Если несколько reader зашли в критическую секцию, то все ок.

Предположим, что зашло два writer, но при захвате блокировки, при обращении к write один из потоков должен был увидеть, что write = true, так как все изменениея write находятся в секциях обеспечивающих happens-before, а значит, один из них уснет и данная ситуация не возможна.

Предположим, что reader и writer в критической секции, то по аналогии выше один из двух должен был увидеть, что либо writer = true, либо reader !=0, так как изменения обоих этих переменных в synchronized, то их можно расставить по времени.

А значит неправильно поставленной цеди для ReadWriteLock не произойдет




