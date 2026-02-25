package com.devx27.app.presentation.editor

// ─────────────────────────────────────────────────────────────────────────────
// SyntaxHighlighter — regex-based, language-agnostic tokeniser
// Returns a list of SyntaxToken covering every character span in the source.
// ─────────────────────────────────────────────────────────────────────────────
object SyntaxHighlighter {

    // ── Python keyword set ────────────────────────────────────────────────────
    private val PYTHON_KEYWORDS = setOf(
        "False", "None", "True", "and", "as", "assert", "async", "await",
        "break", "class", "continue", "def", "del", "elif", "else", "except",
        "finally", "for", "from", "global", "if", "import", "in", "is",
        "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try",
        "while", "with", "yield", "self", "cls", "print", "range", "len",
        "type", "int", "str", "float", "list", "dict", "set", "bool", "tuple"
    )

    private val CPP_KEYWORDS = setOf(
        "alignas", "alignof", "and", "and_eq", "asm", "atomic_cancel", "atomic_commit",
        "atomic_noexcept", "auto", "bitand", "bitor", "bool", "break", "case", "catch",
        "char", "char8_t", "char16_t", "char32_t", "class", "compl", "concept", "const",
        "consteval", "constexpr", "constinit", "const_cast", "continue", "co_await",
        "co_return", "co_yield", "decltype", "default", "delete", "do", "double",
        "dynamic_cast", "else", "enum", "explicit", "export", "extern", "false",
        "float", "for", "friend", "goto", "if", "inline", "int", "long", "mutable",
        "namespace", "new", "noexcept", "not", "not_eq", "nullptr", "operator", "or",
        "or_eq", "private", "protected", "public", "reflexpr", "register",
        "reinterpret_cast", "requires", "return", "short", "signed", "sizeof", "static",
        "static_assert", "static_cast", "struct", "switch", "template", "this",
        "thread_local", "throw", "true", "try", "typedef", "typeid", "typename",
        "union", "unsigned", "using", "virtual", "void", "volatile", "wchar_t",
        "while", "xor", "xor_eq", "std", "cout", "cin", "vector", "string"
    )

    private val JAVA_KEYWORDS = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new", "package",
        "private", "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws", "transient",
        "try", "void", "volatile", "while", "true", "false", "null", "String", "System"
    )

    private val JS_KEYWORDS = setOf(
        "await", "break", "case", "catch", "class", "const", "continue", "debugger",
        "default", "delete", "do", "else", "enum", "export", "extends", "false",
        "finally", "for", "function", "if", "import", "in", "instanceof", "new",
        "null", "return", "super", "switch", "this", "throw", "true", "try", "typeof",
        "var", "void", "while", "with", "yield", "let", "static", "arguments",
        "async", "await", "console", "log", "window", "document"
    )

    // ── Kotlin keyword set ────────────────────────────────────────────────────
    private val KOTLIN_KEYWORDS = setOf(
        "abstract", "actual", "as", "break", "by", "catch", "class", "companion",
        "const", "constructor", "continue", "crossinline", "data", "delegate",
        "do", "dynamic", "else", "enum", "expect", "external", "false", "field",
        "file", "final", "finally", "for", "fun", "get", "if", "import", "in",
        "infix", "init", "inline", "inner", "interface", "internal", "is",
        "it", "lateinit", "noinline", "null", "object", "open", "operator",
        "out", "override", "package", "param", "private", "property", "protected",
        "public", "reified", "return", "sealed", "set", "super", "suspend",
        "tailrec", "this", "throw", "true", "try", "typealias", "typeof",
        "val", "value", "var", "vararg", "when", "where", "while",
        // Common types
        "Int", "Long", "Float", "Double", "Boolean", "String", "Unit", "Any",
        "Nothing", "List", "Map", "Set", "MutableList", "MutableMap", "Flow"
    )

    enum class Language { PYTHON, KOTLIN, CPP, JAVA, JAVASCRIPT }

    // ── Token regex rules (order matters — more specific first) ───────────────
    private val TRIPLE_STRING = Regex("""\"\"\"[\s\S]*?\"\"\"|\'\'\'[\s\S]*?\'\'\'""")
    private val STRING_DQ     = Regex(""""([^"\\]|\\.)*"""")
    private val STRING_SQ     = Regex("""'([^'\\]|\\.)*'""")
    private val STRING_TICK   = Regex("""`([^`\\]|\\.)*`""")
    private val COMMENT_HASH  = Regex("""#.*""")
    private val COMMENT_SLASH = Regex("""//.*""")
    private val COMMENT_BLOCK = Regex("""/\*[\s\S]*?\*/""")
    private val ANNOTATION    = Regex("""@\w+""")
    private val NUMBER        = Regex("""\b\d+(\.\d+)?([eE][+-]?\d+)?\b""")
    private val IDENTIFIER    = Regex("""\b[A-Za-z_][A-Za-z0-9_]*\b""")
    private val OPERATORS     = Regex("""[+\-*/%=<>!&|^~?:]+|->|=>|\.\.""")

    /**
     * Tokenise [source] for the given [language].
     * Returns a stable list of non-overlapping, sorted SyntaxToken objects.
     */
    fun tokenize(source: String, language: Language): List<SyntaxToken> {
        val keywords = when(language) {
            Language.PYTHON     -> PYTHON_KEYWORDS
            Language.KOTLIN     -> KOTLIN_KEYWORDS
            Language.CPP        -> CPP_KEYWORDS
            Language.JAVA       -> JAVA_KEYWORDS
            Language.JAVASCRIPT -> JS_KEYWORDS
        }
        val tokens   = mutableListOf<SyntaxToken>()
        val covered  = BooleanArray(source.length)

        fun addToken(start: Int, end: Int, type: TokenType) {
            if (start >= end) return
            tokens.add(SyntaxToken(start, end, type))
            for (i in start until end.coerceAtMost(source.length)) covered[i] = true
        }

        fun applyRegex(regex: Regex, type: TokenType) {
            regex.findAll(source).forEach { m ->
                val s = m.range.first; val e = m.range.last + 1
                if (s < source.length && !covered[s]) addToken(s, e, type)
            }
        }

        // Order: block comments → line comments → strings → annotations → numbers → identifiers → operators
        if (language == Language.PYTHON) {
            applyRegex(TRIPLE_STRING, TokenType.STRING)
            applyRegex(COMMENT_HASH, TokenType.COMMENT)
        } else {
            applyRegex(COMMENT_BLOCK, TokenType.COMMENT)
            applyRegex(COMMENT_SLASH, TokenType.COMMENT)
        }
        if (language == Language.JAVASCRIPT) {
            applyRegex(STRING_TICK, TokenType.STRING)
        }
        applyRegex(STRING_DQ,  TokenType.STRING)
        applyRegex(STRING_SQ,  TokenType.STRING)
        applyRegex(ANNOTATION, TokenType.ANNOTATION)
        applyRegex(NUMBER,     TokenType.NUMBER)

        // Keywords and identifiers
        IDENTIFIER.findAll(source).forEach { m ->
            val s = m.range.first; val e = m.range.last + 1
            if (s < source.length && !covered[s]) {
                val word = m.value
                val type = when {
                    keywords.contains(word)                    -> TokenType.KEYWORD
                    word.first().isUpperCase()                  -> TokenType.TYPE
                    m.range.last + 1 < source.length &&
                        source[m.range.last + 1] == '('        -> TokenType.FUNCTION
                    else                                        -> TokenType.PLAIN
                }
                addToken(s, e, type)
            }
        }

        applyRegex(OPERATORS, TokenType.OPERATOR)

        return tokens.sortedBy { it.start }
    }
}
