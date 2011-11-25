$ ->
    $(".story").click ->
        $(".story")
            .hide()
            .eq(($(@).index()+1) % 2)
            .show()