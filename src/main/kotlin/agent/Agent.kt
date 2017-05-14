package agent

import org.objectweb.asm.*
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

private object TargetMethod {
    val ownerClassName = "example/CoroutineExampleKt"
    val name = "test"
    val descr = "(Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"

    fun isTargetMethodCall(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) =
            opcode == Opcodes.INVOKESTATIC &&
                    owner == this.ownerClassName &&
                    name == this.name &&
                    desc == this.descr &&
                    !itf
}

class Agent {
    private class TestDetector : ClassFileTransformer {
        override fun transform(
                loader: ClassLoader?,
                className: String,
                classBeingRedefined: Class<*>?,
                protectionDomain: ProtectionDomain,
                classfileBuffer: ByteArray
        ): ByteArray? {
            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            val adapter = TestAdapter(writer)
            val reader = ClassReader(classfileBuffer)
            reader.accept(adapter, 0)
            return writer.toByteArray()
        }
    }

    private class TestAdapter(
            cv: ClassVisitor
    ) : ClassVisitor(Opcodes.ASM5, cv) {
        override fun visitMethod(access: Int, name: String, desc: String,
                                 signature: String?, exceptions: Array<out String>?
        ): MethodVisitor {
            val mv: MethodVisitor? = super.visitMethod(access, name, desc, signature, exceptions)
            return MethodModifier(mv)
        }
    }

    private class MethodModifier(
            mv: MethodVisitor?
    ) : MethodVisitor(Opcodes.ASM5, mv) {
        override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {
            if (TargetMethod.isTargetMethodCall(opcode, owner, name, desc, itf)) {
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                super.visitLdcInsn("Test detected")
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                        "println", "(Ljava/lang/String;)V", false)

            }
            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

    companion object {
        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("Agent started.")
            inst.addTransformer(TestDetector())
        }
    }
}


