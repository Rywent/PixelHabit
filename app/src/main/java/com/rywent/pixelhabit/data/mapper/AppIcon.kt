// data/mapper/AppIcon.kt
package com.rywent.pixelhabit.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppIcon(val key: String, val icon: ImageVector) {
    // Health & Fitness
    FAVORITE("Favorite", Icons.Default.Favorite),
    FITNESS_CENTER("FitnessCenter", Icons.Default.FitnessCenter),
    WATER_DROP("WaterDrop", Icons.Default.WaterDrop),
    RUN_CIRCLE("RunCircle", Icons.Default.RunCircle),
    SELF_IMPROVEMENT("SelfImprovement", Icons.Default.SelfImprovement),
    BEDTIME("Bedtime", Icons.Default.Bedtime),
    LOCAL_HOSPITAL("LocalHospital", Icons.Default.LocalHospital),
    MONITOR_HEART("MonitorHeart", Icons.Default.MonitorHeart),
    EMOJI_FOOD_BEVERAGE("EmojiFoodBeverage", Icons.Default.EmojiFoodBeverage),
    DIRECTIONS_RUN("DirectionsRun", Icons.AutoMirrored.Filled.DirectionsRun),
    POOL("Pool", Icons.Default.Pool),
    SPORTS_GYMNASTICS("SportsGymnastics", Icons.Default.SportsGymnastics),
    CAKE("Cake", Icons.Default.Cake),
    RESTAURANT("Restaurant", Icons.Default.Restaurant),
    NO_FOOD("NoFood", Icons.Default.NoFood),
    VACCINES("Vaccines", Icons.Default.Vaccines),
    SICK("Sick", Icons.Default.Sick),
    HEALING("Healing", Icons.Default.Healing),
    ACCESSIBILITY_NEW("AccessibilityNew", Icons.Default.AccessibilityNew),

    // Mindfulness & Productivity
    PSYCHOLOGY("Psychology", Icons.Default.Psychology),
    LIGHTBULB("Lightbulb", Icons.Default.Lightbulb),
    EDIT("Edit", Icons.Default.Edit),
    BOOK("Book", Icons.Default.Book),
    MENU_BOOK("MenuBook", Icons.AutoMirrored.Filled.MenuBook),
    CALENDAR_TODAY("CalendarToday", Icons.Default.CalendarToday),
    TIMER("Timer", Icons.Default.Timer),
    ALARM("Alarm", Icons.Default.Alarm),
    CHECK_CIRCLE("CheckCircle", Icons.Default.CheckCircle),
    TASK("Task", Icons.Default.Task),
    WORK("Work", Icons.Default.Work),
    BALANCE("Balance", Icons.Default.Balance),
    EMOJI_OBJECTS("EmojiObjects", Icons.Default.EmojiObjects),
    WB_SUNNY("WbSunny", Icons.Default.WbSunny),
    NIGHTLIGHT("Nightlight", Icons.Default.Nightlight),
    PENDING("Pending", Icons.Default.Pending),
    THUMB_UP("ThumbUp", Icons.Default.ThumbUp),

    // Learning & Growth
    SCHOOL("School", Icons.Default.School),
    LANGUAGE("Language", Icons.Default.Language),
    CODE("Code", Icons.Default.Code),
    PALETTE("Palette", Icons.Default.Palette),
    MUSIC_NOTE("MusicNote", Icons.Default.MusicNote),
    SPORTS_ESPORTS("SportsEsports", Icons.Default.SportsEsports),
    CREATE("Create", Icons.Default.Create),
    AUTO_STORIES("AutoStories", Icons.Default.AutoStories),
    QUIZ("Quiz", Icons.Default.Quiz),
    TRANSLATE("Translate", Icons.Default.Translate),
    DEVELOPER_MODE("DeveloperMode", Icons.Default.DeveloperMode),
    SCIENCE("Science", Icons.Default.Science),
    PUBLIC("Public", Icons.Default.Public),
    HISTORY_EDU("HistoryEdu", Icons.Default.HistoryEdu),
    THEATER_COMEDY("TheaterComedy", Icons.Default.TheaterComedy),
    BRUSH("Brush", Icons.Default.Brush),
    AUDIOTRACK("Audiotrack", Icons.Default.Audiotrack),
    VIDEO_LIBRARY("VideoLibrary", Icons.Default.VideoLibrary),
    LOCAL_LIBRARY("LocalLibrary", Icons.Default.LocalLibrary),

    // Food & Cooking
    FREE_BREAKFAST("FreeBreakfast", Icons.Default.FreeBreakfast),
    LUNCH_DINING("LunchDining", Icons.Default.LunchDining),
    DINNER_DINING("DinnerDining", Icons.Default.DinnerDining),
    LOCAL_CAFE("LocalCafe", Icons.Default.LocalCafe),
    RAMEN_DINING("RamenDining", Icons.Default.RamenDining),
    TAKEOUT_DINING("TakeoutDining", Icons.Default.TakeoutDining),
    EGG("Egg", Icons.Default.Egg),
    COOKIE("Cookie", Icons.Default.Cookie),
    ICECREAM("Icecream", Icons.Default.Icecream),
    FASTFOOD("Fastfood", Icons.Default.Fastfood),
    BAKERY_DINING("BakeryDining", Icons.Default.BakeryDining),
    BRUNCH_DINING("BrunchDining", Icons.Default.BrunchDining),
    HEALTH_AND_SAFETY("HealthAndSafety", Icons.Default.HealthAndSafety),

    // Chores & Housekeeping
    CLEAN_HANDS("CleanHands", Icons.Default.CleanHands),
    SOAP("Soap", Icons.Default.Soap),
    HOUSE("House", Icons.Default.House),
    HOUSE_SIDING("HouseSiding", Icons.Default.HouseSiding),
    OTHER_HOUSES("OtherHouses", Icons.Default.OtherHouses),
    BED("Bed", Icons.Default.Bed),
    DOOR_FRONT("DoorFront", Icons.Default.DoorFront),
    GARAGE("Garage", Icons.Default.Garage),
    WINDOW("Window", Icons.Default.Window),

    // Lifestyle & Daily
    HOME("Home", Icons.Default.Home),
    SHOPPING_CART("ShoppingCart", Icons.Default.ShoppingCart),
    DIRECTIONS_CAR("DirectionsCar", Icons.Default.DirectionsCar),
    TRAIN("Train", Icons.Default.Train),
    FLIGHT("Flight", Icons.Default.Flight),
    COFFEE("Coffee", Icons.Default.Coffee),
    LOCAL_LAUNDRY("LocalLaundryService", Icons.Default.LocalLaundryService),
    KITCHEN("Kitchen", Icons.Default.Kitchen),
    PETS("Pets", Icons.Default.Pets),
    CHILD_CARE("ChildCare", Icons.Default.ChildCare),
    GROUP("Group", Icons.Default.Group),
    FAMILY_RESTROOM("FamilyRestroom", Icons.Default.FamilyRestroom),
    SHOWER("Shower", Icons.Default.Shower),
    BATHTUB("Bathtub", Icons.Default.Bathtub),
    WC("Wc", Icons.Default.Wc),
    PHONE("Phone", Icons.Default.Phone),
    EMAIL("Email", Icons.Default.Email),
    CHAT("Chat", Icons.AutoMirrored.Filled.Chat),
    NOTIFICATIONS("Notifications", Icons.Default.Notifications),
    STAR("Star", Icons.Default.Star),

    // Nature & Environment
    ECO("Eco", Icons.Default.Eco),
    PARK("Park", Icons.Default.Park),
    FOREST("Forest", Icons.Default.Forest),
    WATER("Water", Icons.Default.Water),
    WB_CLOUDY("WbCloudy", Icons.Default.WbCloudy),
    SEVERE_COLD("SevereCold", Icons.Default.SevereCold),
    THERMOSTAT("Thermostat", Icons.Default.Thermostat),
    ENERGY_SAVINGS_LEAF("EnergySavingsLeaf", Icons.Default.EnergySavingsLeaf),
    LOCAL_FLORIST("LocalFlorist", Icons.Default.LocalFlorist),
    BUG_REPORT("BugReport", Icons.Default.BugReport),
    SPA("Spa", Icons.Default.Spa),
    CLOUD("Cloud", Icons.Default.Cloud),
    THUNDERSTORM("Thunderstorm", Icons.Default.Thunderstorm),
    BEACH_ACCESS("BeachAccess", Icons.Default.BeachAccess),
    HIKING("Hiking", Icons.Default.Hiking),
    SURFING("Surfing", Icons.Default.Surfing),
    SAILING("Sailing", Icons.Default.Sailing),
    DOWNHILL_SKIING("DownhillSkiing", Icons.Default.DownhillSkiing),
    QUESTION_MARK("QuestionMark", Icons.Default.QuestionMark),
    CLOSE("Close", Icons.Default.Close),


    WB_SUNNY_FILLED("WbSunny", Icons.Default.WbSunny),
    WB_TWILIGHT_FILLED("WbTwilight", Icons.Default.WbTwilight),
    NIGHTLIGHT_FILLED("Nightlight", Icons.Default.NightlightRound),
    SCHEDULE_FILLED("Schedule", Icons.Default.Schedule),

    LOCAL_FIRE_DEPARTMENT("LocalFireDepartment", Icons.Default.LocalFireDepartment),
    APPS("Apps", Icons.Default.Apps),
    KEYBOARD_ARROW_UP("KeyboardArrowUp", Icons.Default.KeyboardArrowUp),
    KEYBOARD_ARROW_DOWN("KeyboardArrowDown", Icons.Default.KeyboardArrowDown);



    companion object {
        fun fromKey(key: String): ImageVector {
            return entries.firstOrNull { it.key == key }?.icon ?: QUESTION_MARK.icon
        }

        fun fromIcon(icon: ImageVector): String {
            return entries.firstOrNull { it.icon == icon }?.key ?: "QuestionMark"
        }
    }
}