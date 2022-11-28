package com.sophiadiagrams.avedex.lib.services.image_analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.sophiadiagrams.avedex.lib.services.retrofit.BirdsResponse
import com.sophiadiagrams.avedex.lib.services.retrofit.RetrofitService
import com.sophiadiagrams.avedex.ml.Avedex
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.nio.ByteBuffer
import java.nio.ByteOrder

val BIRDS_NAMES: List<String> = listOf(
    "ABBOTTS BABBLER",
    "ABBOTTS BOOBY",
    "ABYSSINIAN GROUND HORNBILL",
    "AFRICAN CROWNED CRANE",
    "AFRICAN EMERALD CUCKOO",
    "AFRICAN FIREFINCH",
    "AFRICAN OYSTER CATCHER",
    "AFRICAN PIED HORNBILL",
    "ALBATROSS",
    "ALBERTS TOWHEE",
    "ALEXANDRINE PARAKEET",
    "ALPINE CHOUGH",
    "ALTAMIRA YELLOWTHROAT",
    "AMERICAN AVOCET",
    "AMERICAN BITTERN",
    "AMERICAN COOT",
    "AMERICAN FLAMINGO",
    "AMERICAN GOLDFINCH",
    "AMERICAN KESTREL",
    "AMERICAN PIPIT",
    "AMERICAN REDSTART",
    "AMERICAN WIGEON",
    "AMETHYST WOODSTAR",
    "ANDEAN GOOSE",
    "ANDEAN LAPWING",
    "ANDEAN SISKIN",
    "ANHINGA",
    "ANIANIAU",
    "ANNAS HUMMINGBIRD",
    "ANTBIRD",
    "ANTILLEAN EUPHONIA",
    "APAPANE",
    "APOSTLEBIRD",
    "ARARIPE MANAKIN",
    "ASHY STORM PETREL",
    "ASHY THRUSHBIRD",
    "ASIAN CRESTED IBIS",
    "ASIAN DOLLARD BIRD",
    "AUCKLAND SHAQ",
    "AUSTRAL CANASTERO",
    "AUSTRALASIAN FIGBIRD",
    "AVADAVAT",
    "AZARAS SPINETAIL",
    "AZURE BREASTED PITTA",
    "AZURE JAY",
    "AZURE TANAGER",
    "AZURE TIT",
    "BAIKAL TEAL",
    "BALD EAGLE",
    "BALD IBIS",
    "BALI STARLING",
    "BALTIMORE ORIOLE",
    "BANANAQUIT",
    "BAND TAILED GUAN",
    "BANDED BROADBILL",
    "BANDED PITA",
    "BANDED STILT",
    "BAR-TAILED GODWIT",
    "BARN OWL",
    "BARN SWALLOW",
    "BARRED PUFFBIRD",
    "BARROWS GOLDENEYE",
    "BAY-BREASTED WARBLER",
    "BEARDED BARBET",
    "BEARDED BELLBIRD",
    "BEARDED REEDLING",
    "BELTED KINGFISHER",
    "BIRD OF PARADISE",
    "BLACK & YELLOW BROADBILL",
    "BLACK BAZA",
    "BLACK COCKATO",
    "BLACK FRANCOLIN",
    "BLACK SKIMMER",
    "BLACK SWAN",
    "BLACK TAIL CRAKE",
    "BLACK THROATED BUSHTIT",
    "BLACK THROATED WARBLER",
    "BLACK VENTED SHEARWATER",
    "BLACK VULTURE",
    "BLACK-CAPPED CHICKADEE",
    "BLACK-NECKED GREBE",
    "BLACK-THROATED SPARROW",
    "BLACKBURNIAM WARBLER",
    "BLONDE CRESTED WOODPECKER",
    "BLOOD PHEASANT",
    "BLUE COAU",
    "BLUE DACNIS",
    "BLUE GROUSE",
    "BLUE HERON",
    "BLUE MALKOHA",
    "BLUE THROATED TOUCANET",
    "BOBOLINK",
    "BORNEAN BRISTLEHEAD",
    "BORNEAN LEAFBIRD",
    "BORNEAN PHEASANT",
    "BRANDT CORMARANT",
    "BREWERS BLACKBIRD",
    "BROWN CREPPER",
    "BROWN NOODY",
    "BROWN THRASHER",
    "BUFFLEHEAD",
    "BULWERS PHEASANT",
    "BURCHELLS COURSER",
    "BUSH TURKEY",
    "CAATINGA CACHOLOTE",
    "CACTUS WREN",
    "CALIFORNIA CONDOR",
    "CALIFORNIA GULL",
    "CALIFORNIA QUAIL",
    "CAMPO FLICKER",
    "CANARY",
    "CAPE GLOSSY STARLING",
    "CAPE LONGCLAW",
    "CAPE MAY WARBLER",
    "CAPE ROCK THRUSH",
    "CAPPED HERON",
    "CAPUCHINBIRD",
    "CARMINE BEE-EATER",
    "CASPIAN TERN",
    "CASSOWARY",
    "CEDAR WAXWING",
    "CERULEAN WARBLER",
    "CHARA DE COLLAR",
    "CHATTERING LORY",
    "CHESTNET BELLIED EUPHONIA",
    "CHINESE BAMBOO PARTRIDGE",
    "CHINESE POND HERON",
    "CHIPPING SPARROW",
    "CHUCAO TAPACULO",
    "CHUKAR PARTRIDGE",
    "CINNAMON ATTILA",
    "CINNAMON FLYCATCHER",
    "CINNAMON TEAL",
    "CLARKS NUTCRACKER",
    "COCK OF THE  ROCK",
    "COCKATOO",
    "COLLARED ARACARI",
    "COMMON FIRECREST",
    "COMMON GRACKLE",
    "COMMON HOUSE MARTIN",
    "COMMON IORA",
    "COMMON LOON",
    "COMMON POORWILL",
    "COMMON STARLING",
    "COPPERY TAILED COUCAL",
    "CRAB PLOVER",
    "CRANE HAWK",
    "CREAM COLORED WOODPECKER",
    "CRESTED AUKLET",
    "CRESTED CARACARA",
    "CRESTED COUA",
    "CRESTED FIREBACK",
    "CRESTED KINGFISHER",
    "CRESTED NUTHATCH",
    "CRESTED OROPENDOLA",
    "CRESTED SHRIKETIT",
    "CRIMSON CHAT",
    "CRIMSON SUNBIRD",
    "CROW",
    "CROWNED PIGEON",
    "CUBAN TODY",
    "CUBAN TROGON",
    "CURL CRESTED ARACURI",
    "D-ARNAUDS BARBET",
    "DALMATIAN PELICAN",
    "DARJEELING WOODPECKER",
    "DARK EYED JUNCO",
    "DARWINS FLYCATCHER",
    "DAURIAN REDSTART",
    "DEMOISELLE CRANE",
    "DOUBLE BARRED FINCH",
    "DOUBLE BRESTED CORMARANT",
    "DOUBLE EYED FIG PARROT",
    "DOWNY WOODPECKER",
    "DUSKY LORY",
    "DUSKY ROBIN",
    "EARED PITA",
    "EASTERN BLUEBIRD",
    "EASTERN BLUEBONNET",
    "EASTERN GOLDEN WEAVER",
    "EASTERN MEADOWLARK",
    "EASTERN ROSELLA",
    "EASTERN TOWEE",
    "EASTERN WIP POOR WILL",
    "ECUADORIAN HILLSTAR",
    "EGYPTIAN GOOSE",
    "ELEGANT TROGON",
    "ELLIOTS  PHEASANT",
    "EMERALD TANAGER",
    "EMPEROR PENGUIN",
    "EMU",
    "ENGGANO MYNA",
    "EURASIAN BULLFINCH",
    "EURASIAN GOLDEN ORIOLE",
    "EURASIAN MAGPIE",
    "EUROPEAN GOLDFINCH",
    "EUROPEAN TURTLE DOVE",
    "EVENING GROSBEAK",
    "FAIRY BLUEBIRD",
    "FAIRY PENGUIN",
    "FAIRY TERN",
    "FAN TAILED WIDOW",
    "FASCIATED WREN",
    "FIERY MINIVET",
    "FIORDLAND PENGUIN",
    "FIRE TAILLED MYZORNIS",
    "FLAME BOWERBIRD",
    "FLAME TANAGER",
    "FRIGATE",
    "GAMBELS QUAIL",
    "GANG GANG COCKATOO",
    "GILA WOODPECKER",
    "GILDED FLICKER",
    "GLOSSY IBIS",
    "GO AWAY BIRD",
    "GOLD WING WARBLER",
    "GOLDEN BOWER BIRD",
    "GOLDEN CHEEKED WARBLER",
    "GOLDEN CHLOROPHONIA",
    "GOLDEN EAGLE",
    "GOLDEN PARAKEET",
    "GOLDEN PHEASANT",
    "GOLDEN PIPIT",
    "GOULDIAN FINCH",
    "GRANDALA",
    "GRAY CATBIRD",
    "GRAY KINGBIRD",
    "GRAY PARTRIDGE",
    "GREAT GRAY OWL",
    "GREAT JACAMAR",
    "GREAT KISKADEE",
    "GREAT POTOO",
    "GREAT TINAMOU",
    "GREAT XENOPS",
    "GREATER PEWEE",
    "GREATOR SAGE GROUSE",
    "GREEN BROADBILL",
    "GREEN JAY",
    "GREEN MAGPIE",
    "GREY CUCKOOSHRIKE",
    "GREY PLOVER",
    "GROVED BILLED ANI",
    "GUINEA TURACO",
    "GUINEAFOWL",
    "GURNEYS PITTA",
    "GYRFALCON",
    "HAMERKOP",
    "HARLEQUIN DUCK",
    "HARLEQUIN QUAIL",
    "HARPY EAGLE",
    "HAWAIIAN GOOSE",
    "HAWFINCH",
    "HELMET VANGA",
    "HEPATIC TANAGER",
    "HIMALAYAN BLUETAIL",
    "HIMALAYAN MONAL",
    "HOATZIN",
    "HOODED MERGANSER",
    "HOOPOES",
    "HORNED GUAN",
    "HORNED LARK",
    "HORNED SUNGEM",
    "HOUSE FINCH",
    "HOUSE SPARROW",
    "HYACINTH MACAW",
    "IBERIAN MAGPIE",
    "IBISBILL",
    "IMPERIAL SHAQ",
    "INCA TERN",
    "INDIAN BUSTARD",
    "INDIAN PITTA",
    "INDIAN ROLLER",
    "INDIAN VULTURE",
    "INDIGO BUNTING",
    "INDIGO FLYCATCHER",
    "INLAND DOTTEREL",
    "IVORY BILLED ARACARI",
    "IVORY GULL",
    "IWI",
    "JABIRU",
    "JACK SNIPE",
    "JANDAYA PARAKEET",
    "JAPANESE ROBIN",
    "JAVA SPARROW",
    "JOCOTOCO ANTPITTA",
    "KAGU",
    "KAKAPO",
    "KILLDEAR",
    "KING EIDER",
    "KING VULTURE",
    "KIWI",
    "KOOKABURRA",
    "LARK BUNTING",
    "LAZULI BUNTING",
    "LESSER ADJUTANT",
    "LILAC ROLLER",
    "LITTLE AUK",
    "LOGGERHEAD SHRIKE",
    "LONG-EARED OWL",
    "MAGPIE GOOSE",
    "MALABAR HORNBILL",
    "MALACHITE KINGFISHER",
    "MALAGASY WHITE EYE",
    "MALEO",
    "MALLARD DUCK",
    "MANDRIN DUCK",
    "MANGROVE CUCKOO",
    "MARABOU STORK",
    "MASKED BOOBY",
    "MASKED LAPWING",
    "MCKAYS BUNTING",
    "MIKADO  PHEASANT",
    "MOURNING DOVE",
    "MYNA",
    "NICOBAR PIGEON",
    "NOISY FRIARBIRD",
    "NORTHERN BEARDLESS TYRANNULET",
    "NORTHERN CARDINAL",
    "NORTHERN FLICKER",
    "NORTHERN FULMAR",
    "NORTHERN GANNET",
    "NORTHERN GOSHAWK",
    "NORTHERN JACANA",
    "NORTHERN MOCKINGBIRD",
    "NORTHERN PARULA",
    "NORTHERN RED BISHOP",
    "NORTHERN SHOVELER",
    "OCELLATED TURKEY",
    "OKINAWA RAIL",
    "ORANGE BRESTED BUNTING",
    "ORIENTAL BAY OWL",
    "OSPREY",
    "OSTRICH",
    "OVENBIRD",
    "OYSTER CATCHER",
    "PAINTED BUNTING",
    "PALILA",
    "PARADISE TANAGER",
    "PARAKETT  AKULET",
    "PARUS MAJOR",
    "PATAGONIAN SIERRA FINCH",
    "PEACOCK",
    "PEREGRINE FALCON",
    "PHILIPPINE EAGLE",
    "PINK ROBIN",
    "POMARINE JAEGER",
    "PUFFIN",
    "PURPLE FINCH",
    "PURPLE GALLINULE",
    "PURPLE MARTIN",
    "PURPLE SWAMPHEN",
    "PYGMY KINGFISHER",
    "QUETZAL",
    "RAINBOW LORIKEET",
    "RAZORBILL",
    "RED BEARDED BEE EATER",
    "RED BELLIED PITTA",
    "RED BROWED FINCH",
    "RED FACED CORMORANT",
    "RED FACED WARBLER",
    "RED FODY",
    "RED HEADED DUCK",
    "RED HEADED WOODPECKER",
    "RED HONEY CREEPER",
    "RED NAPED TROGON",
    "RED TAILED HAWK",
    "RED TAILED THRUSH",
    "RED WINGED BLACKBIRD",
    "RED WISKERED BULBUL",
    "REGENT BOWERBIRD",
    "RING-NECKED PHEASANT",
    "ROADRUNNER",
    "ROBIN",
    "ROCK DOVE",
    "ROSY FACED LOVEBIRD",
    "ROUGH LEG BUZZARD",
    "ROYAL FLYCATCHER",
    "RUBY THROATED HUMMINGBIRD",
    "RUDY KINGFISHER",
    "RUFOUS KINGFISHER",
    "RUFUOS MOTMOT",
    "SAMATRAN THRUSH",
    "SAND MARTIN",
    "SANDHILL CRANE",
    "SATYR TRAGOPAN",
    "SCARLET CROWNED FRUIT DOVE",
    "SCARLET IBIS",
    "SCARLET MACAW",
    "SCARLET TANAGER",
    "SHOEBILL",
    "SHORT BILLED DOWITCHER",
    "SKUA",
    "SMITHS LONGSPUR",
    "SNOWY EGRET",
    "SNOWY OWL",
    "SNOWY PLOVER",
    "SORA",
    "SPANGLED COTINGA",
    "SPLENDID WREN",
    "SPOON BILED SANDPIPER",
    "SPOONBILL",
    "SPOTTED CATBIRD",
    "SRI LANKA BLUE MAGPIE",
    "STEAMER DUCK",
    "STORK BILLED KINGFISHER",
    "STRAWBERRY FINCH",
    "STRIPED OWL",
    "STRIPPED MANAKIN",
    "STRIPPED SWALLOW",
    "SUPERB STARLING",
    "SWINHOES PHEASANT",
    "TAILORBIRD",
    "TAIWAN MAGPIE",
    "TAKAHE",
    "TASMANIAN HEN",
    "TEAL DUCK",
    "TIT MOUSE",
    "TOUCHAN",
    "TOWNSENDS WARBLER",
    "TREE SWALLOW",
    "TRICOLORED BLACKBIRD",
    "TROPICAL KINGBIRD",
    "TRUMPTER SWAN",
    "TURKEY VULTURE",
    "TURQUOISE MOTMOT",
    "UMBRELLA BIRD",
    "VARIED THRUSH",
    "VEERY",
    "VENEZUELIAN TROUPIAL",
    "VERMILION FLYCATHER",
    "VICTORIA CROWNED PIGEON",
    "VIOLET GREEN SWALLOW",
    "VIOLET TURACO",
    "VULTURINE GUINEAFOWL",
    "WALL CREAPER",
    "WATTLED CURASSOW",
    "WATTLED LAPWING",
    "WHIMBREL",
    "WHITE BROWED CRAKE",
    "WHITE CHEEKED TURACO",
    "WHITE CRESTED HORNBILL",
    "WHITE NECKED RAVEN",
    "WHITE TAILED TROPIC",
    "WHITE THROATED BEE EATER",
    "WILD TURKEY",
    "WILSONS BIRD OF PARADISE",
    "WOOD DUCK",
    "YELLOW BELLIED FLOWERPECKER",
    "YELLOW CACIQUE",
    "YELLOW HEADED BLACKBIRD",
)

class ImageAnalyzerService(val context: Context) {
    var utils = Utils
    private var retrofit = RetrofitService()
    private val options = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(5)
        .setScoreThreshold(0.3f)
        .build()

    private val objectDetector = ObjectDetector.createFromFileAndOptions(
        this.context,
        "object-detector.tflite",
        options
    )

    private var birdClassifier: Interpreter? = null

    init {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                "avedex", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    birdClassifier = Interpreter(modelFile)
                }
            }
    }

    fun detect(bitmap: Bitmap): Bitmap? {
        val image = TensorImage.fromBitmap(bitmap)
        val results = objectDetector.detect(image)
        for (detectedObject in results) {
            if (detectedObject.categories.map { it.label }.any { it == "bird" }) {
                val boundingBox = Rect()
                detectedObject.boundingBox.round(boundingBox)
                val padding = 60
                return Bitmap.createBitmap(
                    bitmap,
                    boundingBox.left - padding,
                    boundingBox.top - padding,
                    boundingBox.width() + 2 * padding, // Es dos veces el padding porque tiene que compensar de ambos lados
                    boundingBox.height() + 2 * padding
                )
            }
        }
        return null
    }

    private fun createBitmapForXception(bitmap: Bitmap): ByteBuffer {
        val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)
                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f
                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }
        return input
    }

    suspend fun classify(bitmap: Bitmap): BirdsResponse? {
        var recognizedBird = ""
        if (birdClassifier == null) {
//            val input = createBitmapForXception(bitmap)
            val bytes = bitmap.byteCount;
            val input = ByteBuffer.allocate(bytes);
            bitmap.copyPixelsToBuffer(input)

            val bufferSize = 450 * java.lang.Float.SIZE / java.lang.Byte.SIZE
            val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
            birdClassifier?.run(input, modelOutput)
            modelOutput.rewind()
            var recognizedProbability = .0
            val probabilities = modelOutput.asFloatBuffer()
            for (i in 0 until probabilities.capacity()) {
                val prob = probabilities.get(i)
                if (prob > recognizedProbability) {
                    recognizedProbability = prob.toDouble()
                    recognizedBird = BIRDS_NAMES[i]
                }
            }
        } else {
            val model = Avedex.newInstance(context)
            val input = TensorImage.fromBitmap(bitmap)
            val outputs = model.process(input)
            val probabilities = outputs.probabilityAsCategoryList
            model.close()
            recognizedBird = probabilities.maxBy { a -> a.score }.displayName
        }
        return retrofit.postBirdsData(recognizedBird)
    }
}