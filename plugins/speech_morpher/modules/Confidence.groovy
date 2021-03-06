class Confidence {
    def bridge
    void initialize(bridge){  this.bridge = bridge  }

    static def random = new Random(System.currentTimeMillis())

    int characteristicSum = 0
    void setPreset(Map preset){
        characteristicSum =
                (   preset.get("selfconfidence") +
                    preset.get("attitude") +
           Math.max(preset.get("energy"), 0)) *
          (Math.max(preset.get("impulsivity"), 0) + 2)
    }

    Map morphPhrase(Map phrase){
        if (characteristicSum == 0) phrase
        def text = phrase.get("text")
        text = exclaim(text)
        text = stutter(text)
        phrase.put("text", text)
        return phrase
    }

    def exclaim(text) {

        def textParts = bridge.split(text)
        def finalText = ""
        for (def part : textParts) {
            def i = part[part.size()-1]
            part = part[0..part.size()-2]
            if (!i.contains("...") && (i.contains(".") || i.contains("!") || i.contains("?"))) {
                if (random.nextFloat() * characteristicSum > 6){
                    if (i.contains("."))
                        i = i.replace(".", "!")
                    else if (i.contains("!"))
                        i = i.replace("!", "!!!")
                    else if (i.contains("?"))
                        i = i.replace("?", "?!")
                } else if (characteristicSum < 0){
                    i = i.replace(".", "...")
                }
            }
            boolean c = false
            for (String p : part)
                finalText += ((c = !c) ? " " : "") + p
            finalText += i
        }
        return finalText
    }

    def stutter(text) {
        if (characteristicSum >= 0) return text

        String[] words = text.split(" ")

        def hesitationCount = Math.min(words.length, Math.max(-characteristicSum + random.nextInt(4) - 2, 0))
        if (hesitationCount == 0) return text

        def positions = []
        while (positions.size() < hesitationCount){
            int pos = random.nextInt(words.size())
            if (!positions.contains(pos))
                positions.add(pos)
        }

        for (int pos : positions){
            if (!words[pos].matches('[A-z\\u0400-\\u04FF]+'))
                continue
            int count = 1 + random.nextInt(Math.min(-characteristicSum-1, 4))
            def word = ""
            for (int i=0; i<count; i++)
                word += words[pos][0]+"-"
            word += words[pos][0]
            if (words[pos].length() > 2) word += words[pos][1..words[pos].length()-1]
            else if (words[pos].length() == 2) word += words[pos][1]
            words[pos] = word
        }

        def finalText = ""
        for(int i = 0; i < words.size(); i++) {
            finalText += words[i] + " "
        }
        return finalText.trim()
    }

    String getName(Locale locale){
        switch (locale){
            case new Locale("ru"): return "Смена уверенности"
            case new Locale("en"): return "Selfconfidence changing"
        }
    }
}