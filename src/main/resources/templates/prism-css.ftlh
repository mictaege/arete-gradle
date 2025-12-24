<#-- @ftlvariable name="colorScheme" type="com.github.mictaege.arete_gradle.AreteColorScheme" -->
<style>
    /* PrismJS 1.25.0
https://prismjs.com/download.html#themes=prism-okaidia&languages=javastacktrace */
    code[class*=language-], pre[class*=language-] {
        color: ${colorScheme.arete_color_foreground};
        background: 0 0;
        text-shadow: 0 1px ${colorScheme.arete_color_card};
        font-family: Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace;
        font-size: 1em;
        text-align: left;
        white-space: pre;
        word-spacing: normal;
        word-break: normal;
        word-wrap: normal;
        line-height: 1.5;
        -moz-tab-size: 4;
        -o-tab-size: 4;
        tab-size: 4;
        -webkit-hyphens: none;
        -moz-hyphens: none;
        -ms-hyphens: none;
        hyphens: none
    }

    pre[class*=language-] {
        padding: 1em;
        margin: .5em 0;
        overflow: auto;
        border-radius: .3em
    }

    :not(pre) > code[class*=language-], pre[class*=language-] {
        background: ${colorScheme.arete_color_background}
    }

    :not(pre) > code[class*=language-] {
        padding: .1em;
        border-radius: .3em;
        white-space: normal
    }

    .token.cdata, .token.comment, .token.doctype, .token.prolog {
        color: ${colorScheme.arete_color_card}
    }

    .token.punctuation {
        color: ${colorScheme.arete_color_foreground}
    }

    .token.namespace {
        opacity: .7
    }

    .token.constant, .token.deleted, .token.property, .token.symbol, .token.tag {
        color: ${colorScheme.arete_color_failed}
    }

    .token.boolean, .token.number {
        color: ${colorScheme.arete_color_aborted}
    }

    .token.attr-name, .token.builtin, .token.char, .token.inserted, .token.selector, .token.string {
        color: ${colorScheme.arete_color_successful}
    }

    .language-css .token.string, .style .token.string, .token.entity, .token.operator, .token.url, .token.variable {
        color: ${colorScheme.arete_color_foreground}
    }

    .token.atrule, .token.attr-value, .token.class-name, .token.function {
        color: ${colorScheme.arete_color_aborted}
    }

    .token.keyword {
        color: ${colorScheme.arete_color_neutral}
    }

    .token.important, .token.regex {
        color: ${colorScheme.arete_color_aborted_emph}
    }

    .token.bold, .token.important {
        font-weight: 700
    }

    .token.italic {
        font-style: italic
    }

    .token.entity {
        cursor: help
    }

</style>