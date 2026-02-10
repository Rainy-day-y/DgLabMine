package cn.sweetberry.mcmod.dglab.jexl

import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

object JexlEngine {
    // 全局单例 Engine
    val ENGINE: JexlEngine = JexlBuilder()
        .strict(true)
        .silent(false)
        .create()

    // 表达式缓存，key = 条件字符串
    private val expressionCache = mutableMapOf<String, JexlExpression>()

    // 获取或创建表达式
    fun getExpression(exprString: String): JexlExpression {
        return expressionCache.computeIfAbsent(exprString,ENGINE::createExpression)
    }

    // 清空缓存，用于 reload 配置
    fun clearCache() {
        expressionCache.clear()
    }
}