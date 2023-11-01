package studio.attect.tool

import java.util.concurrent.atomic.AtomicInteger

object ImageWorker {
    private val workerList = ArrayList<WorkerThread>()

    val cores = Runtime.getRuntime().availableProcessors()

    var startNanoTime = 0L

    val workerDoneCount = AtomicInteger()

    val commitLock = Any()
    val waitLock = Any()

    init {
        println("处理线程数:$cores")
        repeat(cores) {
            val workerThread = WorkerThread(it)
            workerThread.start()
            workerList.add(workerThread)
        }
    }

    fun commitJob(block: (coreIndex: Int) -> Unit) {
        synchronized(commitLock) {
            startNanoTime = System.nanoTime()
            workerDoneCount.set(cores)
            repeat(cores) { index ->
                workerList[index].addJob(block)
            }
//            println("AAAAA")
            println("唤醒的时间:${System.nanoTime() - startNanoTime}")
            synchronized(waitLock) {
                if (workerDoneCount.get() > 0) {
                    waitLock.wait()
                }
            }

//            println("BBBBB")
        }

    }

    fun Any.wait() {
        (this as Object).wait()
    }

    fun Any.notifyAll() {
        (this as Object).notify()
    }

    class WorkerThread(val index: Int) : Thread() {
        private val lock = Any()

        private var jobContent: ((index: Int) -> Unit)? = null

        init {
            name = "ImageWorker-$index"
        }

        override fun run() {
            while (!isInterrupted) {
//                println("workerReady:$index")
                synchronized(lock) {
                    lock.wait()
                }
//                println("jobStart:$index")
                doJob()
            }
        }

        fun addJob(block: (coreIndex: Int) -> Unit) {
            jobContent = block
            synchronized(lock) {
                lock.notifyAll()
            }
//            println("addJob:$index")
        }

        private fun doJob() {
            jobContent?.invoke(index)
//            println("jobDone:$index")
            if (workerDoneCount.decrementAndGet() == 0) {
                synchronized(waitLock) {
                    waitLock.notifyAll()
                }
            }

        }


    }
}