package com.nutrition.tracker.data

import com.nutrition.tracker.domain.model.Food

internal val sampleFoods = listOf(
    Food("1",  "Chicken Breast",     "Generic",   100f, "g",  165f, 31f,  0f,   3.6f),
    Food("2",  "Brown Rice",         "Generic",   100f, "g",  216f,  5f, 45f,   1.8f, fiber = 1.8f),
    Food("3",  "Banana",             "Generic",   118f, "g",  105f,  1.3f,27f,  0.4f, fiber = 3.1f, sugar = 14f),
    Food("4",  "Whole Egg",          "Generic",    50f, "g",   78f,  6f,  0.6f, 5f),
    Food("5",  "Oatmeal",            "Quaker",     40f, "g",  150f,  5f, 27f,   3f,   fiber = 4f),
    Food("6",  "Greek Yogurt",       "Chobani",   170f, "g",  100f, 17f,  6f,   0.7f, sugar = 4f),
    Food("7",  "Almonds",            "Generic",    28f, "g",  164f,  6f,  6f,  14f,   fiber = 3.5f),
    Food("8",  "Salmon",             "Generic",   100f, "g",  208f, 20f,  0f,  13f),
    Food("9",  "Sweet Potato",       "Generic",   130f, "g",  112f,  2f, 26f,   0.1f, fiber = 3.8f, sugar = 5.4f),
    Food("10", "Broccoli",           "Generic",    91f, "g",   31f,  2.6f,6f,  0.3f,  fiber = 2.4f),
    Food("11", "Whole Milk",         "Generic",   244f, "ml", 149f,  8f, 12f,   8f,   sugar = 12f),
    Food("12", "Peanut Butter",      "Jif",        32f, "g",  190f,  7f,  7f,  16f,   fiber = 2f),
    Food("13", "Apple",              "Generic",   182f, "g",   95f,  0.5f,25f,  0.3f, fiber = 4.4f, sugar = 19f),
    Food("14", "White Bread",        "Wonder",     30f, "g",   80f,  2.7f,15f,  1f),
    Food("15", "Lentils (cooked)",   "Generic",   100f, "g",  116f,  9f, 20f,   0.4f, fiber = 7.9f),
    Food("16", "Cheddar Cheese",     "Generic",    28f, "g",  113f,  7f,  0.4f, 9f,   sodium = 180f),
    Food("17", "Orange Juice",       "Tropicana", 240f, "ml", 110f,  2f, 26f,   0f,   sugar = 22f),
    Food("18", "Pasta (cooked)",     "Generic",   140f, "g",  220f,  8f, 43f,   1.3f, fiber = 2.5f),
    Food("19", "Avocado",            "Generic",   150f, "g",  240f,  3f, 12f,  22f,   fiber = 10f),
    Food("20", "Cottage Cheese",     "Daisy",     113f, "g",  110f, 13f,  5f,   5f,   sodium = 360f)
)
