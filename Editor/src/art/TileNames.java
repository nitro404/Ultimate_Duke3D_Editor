package art;

import java.util.*;
import utilities.*;

public class TileNames {

	protected HashMap<Integer, String> m_tileNames;
	protected Vector<TileNameChangeListener> m_tileNameChangeListeners;

	protected static final HashMap<Integer, String> DEFAULT_TILE_NAME_ENTRIES = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 2232234892954740386L;
		{
			put(1, "SECTOREFFECTOR");
			put(2, "ACTIVATOR");
			put(3, "TOUCHPLATE");
			put(4, "ACTIVATORLOCKED");
			put(5, "MUSICANDSFX");
			put(6, "LOCATORS");
			put(7, "CYCLER");
			put(8, "MASTERSWITCH");
			put(9, "RESPAWN");
			put(10, "GPSPEED");
			put(13, "FOF");
			put(20, "ARROW");
			put(21, "FIRSTGUNSPRITE");
			put(22, "CHAINGUNSPRITE");
			put(23, "RPGSPRITE");
			put(24, "FREEZESPRITE");
			put(25, "SHRINKERSPRITE");
			put(26, "HEAVYHBOMB");
			put(27, "TRIPBOMBSPRITE");
			put(28, "SHOTGUNSPRITE");
			put(29, "DEVISTATORSPRITE");
			put(30, "HEALTHBOX");
			put(31, "AMMOBOX");
			put(32, "GROWSPRITEICON");
			put(33, "INVENTORYBOX");
			put(37, "FREEZEAMMO");
			put(40, "AMMO");
			put(41, "BATTERYAMMO");
			put(42, "DEVISTATORAMMO");
			put(44, "RPGAMMO");
			put(45, "GROWAMMO");
			put(46, "CRYSTALAMMO");
			put(47, "HBOMBAMMO");
			put(48, "AMMOLOTS");
			put(49, "SHOTGUNAMMO");
			put(51, "COLA");
			put(52, "SIXPAK");
			put(53, "FIRSTAID");
			put(54, "SHIELD");
			put(55, "STEROIDS");
			put(56, "AIRTANK");
			put(57, "JETPACK");
			put(59, "HEATSENSOR");
			put(60, "ACCESSCARD");
			put(61, "BOOTS");
			put(70, "MIRRORBROKE");
			put(78, "CLOUDYOCEAN");
			put(79, "CLOUDYSKIES");
			put(80, "MOONSKY1");
			put(81, "MOONSKY2");
			put(82, "MOONSKY3");
			put(83, "MOONSKY4");
			put(84, "BIGORBIT1");
			put(85, "BIGORBIT2");
			put(86, "BIGORBIT3");
			put(87, "BIGORBIT4");
			put(88, "BIGORBIT5");
			put(89, "LA");
			put(98, "REDSKY1");
			put(99, "REDSKY2");
			put(100, "ATOMICHEALTH");
			put(120, "TECHLIGHT2");
			put(121, "TECHLIGHTBUST2");
			put(122, "TECHLIGHT4");
			put(123, "TECHLIGHTBUST4");
			put(124, "WALLLIGHT4");
			put(125, "WALLLIGHTBUST4");
			put(130, "ACCESSSWITCH");
			put(132, "SLOTDOOR");
			put(134, "LIGHTSWITCH");
			put(136, "SPACEDOORSWITCH");
			put(138, "SPACELIGHTSWITCH");
			put(140, "FRANKENSTINESWITCH");
			put(142, "NUKEBUTTON");
			put(146, "MULTISWITCH");
			put(150, "DOORTILE5");
			put(151, "DOORTILE6");
			put(152, "DOORTILE1");
			put(153, "DOORTILE2");
			put(154, "DOORTILE3");
			put(155, "DOORTILE4");
			put(156, "DOORTILE7");
			put(157, "DOORTILE8");
			put(158, "DOORTILE9");
			put(159, "DOORTILE10");
			put(160, "DOORSHOCK");
			put(162, "DIPSWITCH");
			put(164, "DIPSWITCH2");
			put(166, "TECHSWITCH");
			put(168, "DIPSWITCH3");
			put(170, "ACCESSSWITCH2");
			put(180, "REFLECTWATERTILE");
			put(200, "FLOORSLIME");
			put(230, "BIGFORCE");
			put(247, "EPISODE");
			put(255, "MASKWALL9");
			put(260, "W_LIGHT");
			put(263, "SCREENBREAK1");
			put(264, "SCREENBREAK2");
			put(265, "SCREENBREAK3");
			put(266, "SCREENBREAK4");
			put(267, "SCREENBREAK5");
			put(268, "SCREENBREAK6");
			put(269, "SCREENBREAK7");
			put(270, "SCREENBREAK8");
			put(271, "SCREENBREAK9");
			put(272, "SCREENBREAK10");
			put(273, "SCREENBREAK11");
			put(274, "SCREENBREAK12");
			put(275, "SCREENBREAK13");
			put(285, "MASKWALL1");
			put(293, "W_TECHWALL1");
			put(297, "W_TECHWALL2");
			put(299, "W_TECHWALL15");
			put(301, "W_TECHWALL3");
			put(305, "W_TECHWALL4");
			put(306, "W_TECHWALL10");
			put(307, "W_TECHWALL16");
			put(336, "WATERTILE2");
			put(341, "BPANNEL1");
			put(342, "PANNEL1");
			put(343, "PANNEL2");
			put(344, "WATERTILE");
			put(351, "STATIC");
			put(357, "W_SCREENBREAK");
			put(360, "W_HITTECHWALL3");
			put(361, "W_HITTECHWALL4");
			put(362, "W_HITTECHWALL2");
			put(363, "W_HITTECHWALL1");
			put(387, "MASKWALL10");
			put(391, "MASKWALL11");
			put(395, "DOORTILE22");
			put(407, "FANSPRITE");
			put(411, "FANSPRITEBROKE");
			put(412, "FANSHADOW");
			put(416, "FANSHADOWBROKE");
			put(447, "DOORTILE18");
			put(448, "DOORTILE19");
			put(449, "DOORTILE20");
			put(487, "SPACESHUTTLE");
			put(489, "SATELLITE");
			put(499, "VIEWSCREEN2");
			put(501, "VIEWSCREENBROKE");
			put(502, "VIEWSCREEN");
			put(503, "GLASS");
			put(504, "GLASS2");
			put(510, "STAINGLASS1");
			put(514, "MASKWALL5");
			put(516, "SATELITE");
			put(517, "FUELPOD");
			put(538, "SLIMEPIPE");
			put(546, "CRACK1");
			put(547, "CRACK2");
			put(548, "CRACK3");
			put(549, "CRACK4");
			put(550, "FOOTPRINTS");
			put(551, "DOMELITE");
			put(554, "CAMERAPOLE");
			put(556, "CHAIR1");
			put(557, "CHAIR2");
			put(559, "BROKENCHAIR");
			put(560, "MIRROR");
			put(563, "WATERFOUNTAIN");
			put(567, "WATERFOUNTAINBROKE");
			put(568, "FEMMAG1");
			put(569, "TOILET");
			put(571, "STALL");
			put(573, "STALLBROKE");
			put(577, "FEMMAG2");
			put(578, "REACTOR2");
			put(579, "REACTOR2BURNT");
			put(580, "REACTOR2SPARK");
			put(595, "GRATE1");
			put(596, "BGRATE1");
			put(602, "SOLARPANNEL");
			put(603, "NAKED1");
			put(607, "ANTENNA");
			put(609, "MASKWALL12");
			put(615, "TOILETBROKE");
			put(616, "PIPE2");
			put(617, "PIPE1B");
			put(618, "PIPE3");
			put(619, "PIPE1");
			put(621, "CAMERA1");
			put(626, "BRICK");
			put(630, "SPLINTERWOOD");
			put(633, "PIPE2B");
			put(634, "BOLT1");
			put(640, "W_NUMBERS");
			put(660, "WATERDRIP");
			put(661, "WATERBUBBLE");
			put(662, "WATERBUBBLEMAKER");
			put(663, "W_FORCEFIELD");
			put(669, "VACUUM");
			put(672, "FOOTPRINTS2");
			put(673, "FOOTPRINTS3");
			put(674, "FOOTPRINTS4");
			put(675, "EGG");
			put(678, "SCALE");
			put(680, "CHAIR3");
			put(685, "CAMERALIGHT");
			put(686, "MOVIECAMERA");
			put(689, "IVUNIT");
			put(694, "POT1");
			put(695, "POT2");
			put(697, "POT3");
			put(700, "PIPE3B");
			put(701, "WALLLIGHT3");
			put(702, "WALLLIGHTBUST3");
			put(703, "WALLLIGHT1");
			put(704, "WALLLIGHTBUST1");
			put(705, "WALLLIGHT2");
			put(706, "WALLLIGHTBUST2");
			put(712, "LIGHTSWITCH2");
			put(716, "WAITTOBESEATED");
			put(717, "DOORTILE14");
			put(753, "STATUE");
			put(762, "MIKE");
			put(765, "VASE");
			put(768, "SUSHIPLATE1");
			put(769, "SUSHIPLATE2");
			put(774, "SUSHIPLATE3");
			put(779, "SUSHIPLATE4");
			put(781, "DOORTILE16");
			put(792, "SUSHIPLATE5");
			put(806, "OJ");
			put(830, "MASKWALL13");
			put(859, "HURTRAIL");
			put(860, "POWERSWITCH1");
			put(862, "LOCKSWITCH1");
			put(864, "POWERSWITCH2");
			put(867, "ATM");
			put(869, "STATUEFLASH");
			put(888, "ATMBROKE");
			put(893, "BIGHOLE2");
			put(901, "STRIPEBALL");
			put(902, "QUEBALL");
			put(903, "POCKET");
			put(904, "WOODENHORSE");
			put(908, "TREE1");
			put(910, "TREE2");
			put(911, "CACTUS");
			put(913, "MASKWALL2");
			put(914, "MASKWALL3");
			put(915, "MASKWALL4");
			put(916, "FIREEXT");
			put(921, "TOILETWATER");
			put(925, "NEON1");
			put(926, "NEON2");
			put(939, "CACTUSBROKE");
			put(940, "BOUNCEMINE");
			put(950, "BROKEFIREHYDRENT");
			put(951, "BOX");
			put(952, "BULLETHOLE");
			put(954, "BOTTLE1");
			put(955, "BOTTLE2");
			put(956, "BOTTLE3");
			put(957, "BOTTLE4");
			put(963, "FEMPIC5");
			put(964, "FEMPIC6");
			put(965, "FEMPIC7");
			put(969, "HYDROPLANT");
			put(971, "OCEANSPRITE1");
			put(972, "OCEANSPRITE2");
			put(973, "OCEANSPRITE3");
			put(974, "OCEANSPRITE4");
			put(975, "OCEANSPRITE5");
			put(977, "GENERICPOLE");
			put(978, "CONE");
			put(979, "HANGLIGHT");
			put(981, "HYDRENT");
			put(988, "MASKWALL14");
			put(990, "TIRE");
			put(994, "PIPE5");
			put(995, "PIPE6");
			put(996, "PIPE4");
			put(997, "PIPE4B");
			put(1003, "BROKEHYDROPLANT");
			put(1005, "PIPE5B");
			put(1007, "NEON3");
			put(1008, "NEON4");
			put(1009, "NEON5");
			put(1012, "BOTTLE5");
			put(1013, "BOTTLE6");
			put(1014, "BOTTLE8");
			put(1020, "SPOTLITE");
			put(1022, "HANGOOZ");
			put(1024, "MASKWALL15");
			put(1025, "BOTTLE7");
			put(1026, "HORSEONSIDE");
			put(1031, "GLASSPIECES");
			put(1034, "HORSELITE");
			put(1045, "DONUTS");
			put(1046, "NEON6");
			put(1059, "MASKWALL6");
			put(1060, "CLOCK");
			put(1062, "RUBBERCAN");
			put(1067, "BROKENCLOCK");
			put(1069, "PLUG");
			put(1079, "OOZFILTER");
			put(1082, "FLOORPLASMA");
			put(1088, "REACTOR");
			put(1092, "REACTORSPARK");
			put(1096, "REACTORBURNT");
			put(1102, "DOORTILE15");
			put(1111, "HANDSWITCH");
			put(1113, "CIRCLEPANNEL");
			put(1114, "CIRCLEPANNELBROKE");
			put(1122, "PULLSWITCH");
			put(1124, "MASKWALL8");
			put(1141, "BIGHOLE");
			put(1142, "ALIENSWITCH");
			put(1144, "DOORTILE21");
			put(1155, "HANDPRINTSWITCH");
			put(1157, "BOTTLE10");
			put(1158, "BOTTLE11");
			put(1159, "BOTTLE12");
			put(1160, "BOTTLE13");
			put(1161, "BOTTLE14");
			put(1162, "BOTTLE15");
			put(1163, "BOTTLE16");
			put(1164, "BOTTLE17");
			put(1165, "BOTTLE18");
			put(1166, "BOTTLE19");
			put(1169, "DOORTILE17");
			put(1174, "MASKWALL7");
			put(1175, "JAILBARBREAK");
			put(1178, "DOORTILE11");
			put(1179, "DOORTILE12");
			put(1212, "VENDMACHINE");
			put(1214, "VENDMACHINEBROKE");
			put(1215, "COLAMACHINE");
			put(1217, "COLAMACHINEBROKE");
			put(1221, "CRANEPOLE");
			put(1222, "CRANE");
			put(1225, "BARBROKE");
			put(1226, "BLOODPOOL");
			put(1227, "NUKEBARREL");
			put(1228, "NUKEBARRELDENTED");
			put(1229, "NUKEBARRELLEAKED");
			put(1232, "CANWITHSOMETHING");
			put(1233, "MONEY");
			put(1236, "BANNER");
			put(1238, "EXPLODINGBARREL");
			put(1239, "EXPLODINGBARREL2");
			put(1240, "FIREBARREL");
			put(1247, "SEENINE");
			put(1248, "SEENINEDEAD");
			put(1250, "STEAM");
			put(1255, "CEILINGSTEAM");
			put(1260, "PIPE6B");
			put(1261, "TRANSPORTERBEAM");
			put(1267, "RAT");
			put(1272, "TRASH");
			put(1280, "FEMPIC1");
			put(1289, "FEMPIC2");
			put(1293, "BLANKSCREEN");
			put(1294, "PODFEM1");
			put(1298, "FEMPIC3");
			put(1306, "FEMPIC4");
			put(1312, "FEM1");
			put(1317, "FEM2");
			put(1321, "FEM3");
			put(1323, "FEM5");
			put(1324, "BLOODYPOLE");
			put(1325, "FEM4");
			put(1334, "FEM6");
			put(1335, "FEM6PAD");
			put(1336, "FEM8");
			put(1346, "HELECOPT");
			put(1347, "FETUSJIB");
			put(1348, "HOLODUKE");
			put(1353, "SPACEMARINE");
			put(1355, "INDY");
			put(1358, "FETUS");
			put(1359, "FETUSBROKE");
			put(1352, "MONK");
			put(1354, "LUKE");
			put(1360, "COOLEXPLOSION1");
			put(1380, "WATERSPLASH2");
			put(1390, "FIREVASE");
			put(1393, "SCRATCH");
			put(1395, "FEM7");
			put(1400, "APLAYERTOP");
			put(1405, "APLAYER");
			put(1420, "PLAYERONWATER");
			put(1518, "DUKELYINGDEAD");
			put(1520, "DUKETORSO");
			put(1528, "DUKEGUN");
			put(1536, "DUKELEG");
			put(1550, "SHARK");
			put(1620, "BLOOD");
			put(1625, "FIRELASER");
			put(1630, "TRANSPORTERSTAR");
			put(1636, "SPIT");
			put(1637, "LOOGIE");
			put(1640, "FIST");
			put(1641, "FREEZEBLAST");
			put(1642, "DEVISTATORBLAST");
			put(1646, "SHRINKSPARK");
			put(1647, "TONGUE");
			put(1650, "MORTER");
			put(1656, "SHRINKEREXPLOSION");
			put(1670, "RADIUSEXPLOSION");
			put(1671, "FORCERIPPLE");
			put(1680, "LIZTROOP");
			put(1681, "LIZTROOPRUNNING");
			put(1682, "LIZTROOPSTAYPUT");
			put(1705, "LIZTOP");
			put(1715, "LIZTROOPSHOOT");
			put(1725, "LIZTROOPJETPACK");
			put(1734, "LIZTROOPDSPRITE");
			put(1741, "LIZTROOPONTOILET");
			put(1742, "LIZTROOPJUSTSIT");
			put(1744, "LIZTROOPDUCKING");
			put(1768, "HEADJIB1");
			put(1772, "ARMJIB1");
			put(1776, "LEGJIB1");
			put(1817, "CANNONBALL");
			put(1820, "OCTABRAIN");
			put(1821, "OCTABRAINSTAYPUT");
			put(1845, "OCTATOP");
			put(1855, "OCTADEADSPRITE");
			put(1860, "INNERJAW");
			put(1880, "DRONE");
			put(1890, "EXPLOSION2");
			put(1920, "COMMANDER");
			put(1921, "COMMANDERSTAYPUT");
			put(1960, "RECON");
			put(1975, "TANK");
			put(2000, "PIGCOP");
			put(2001, "PIGCOPSTAYPUT");
			put(2045, "PIGCOPDIVE");
			put(2060, "PIGCOPDEADSPRITE");
			put(2061, "PIGTOP");
			put(2120, "LIZMAN");
			put(2121, "LIZMANSTAYPUT");
			put(2150, "LIZMANSPITTING");
			put(2160, "LIZMANFEEDING");
			put(2165, "LIZMANJUMP");
			put(2185, "LIZMANDEADSPRITE");
			put(2200, "FECES");
			put(2201, "LIZMANHEAD1");
			put(2205, "LIZMANARM1");
			put(2209, "LIZMANLEG1");
			put(2219, "EXPLOSION2BOT");
			put(2235, "USERWEAPON");
			put(2242, "HEADERBAR");
			put(2245, "JIBS1");
			put(2250, "JIBS2");
			put(2255, "JIBS3");
			put(2260, "JIBS4");
			put(2265, "JIBS5");
			put(2270, "BURNING");
			put(2271, "FIRE");
			put(2286, "JIBS6");
			put(2296, "BLOODSPLAT1");
			put(2297, "BLOODSPLAT3");
			put(2298, "BLOODSPLAT2");
			put(2299, "BLOODSPLAT4");
			put(2300, "OOZ");
			put(2309, "OOZ2");
			put(2301, "WALLBLOOD1");
			put(2302, "WALLBLOOD2");
			put(2303, "WALLBLOOD3");
			put(2304, "WALLBLOOD4");
			put(2305, "WALLBLOOD5");
			put(2306, "WALLBLOOD6");
			put(2307, "WALLBLOOD7");
			put(2308, "WALLBLOOD8");
			put(2310, "BURNING2");
			put(2311, "FIRE2");
			put(2324, "CRACKKNUCKLES");
			put(2329, "SMALLSMOKE");
			put(2330, "SMALLSMOKEMAKER");
			put(2333, "FLOORFLAME");
			put(2360, "ROTATEGUN");
			put(2370, "GREENSLIME");
			put(2380, "WATERDRIPSPLASH");
			put(2390, "SCRAP6");
			put(2400, "SCRAP1");
			put(2404, "SCRAP2");
			put(2408, "SCRAP3");
			put(2412, "SCRAP4");
			put(2416, "SCRAP5");
			put(2420, "ORGANTIC");
			put(2440, "BETAVERSION");
			put(2442, "PLAYERISHERE");
			put(2443, "PLAYERWASHERE");
			put(2444, "SELECTDIR");
			put(2445, "F1HELP");
			put(2446, "NOTCHON");
			put(2447, "NOTCHOFF");
			put(2448, "GROWSPARK");
			put(2452, "DUKEICON");
			put(2453, "BADGUYICON");
			put(2454, "FOODICON");
			put(2455, "GETICON");
			put(2456, "MENUSCREEN");
			put(2457, "MENUBAR");
			put(2458, "KILLSICON");
			put(2460, "FIRSTAID_ICON");
			put(2461, "HEAT_ICON");
			put(2462, "BOTTOMSTATUSBAR");
			put(2463, "BOOT_ICON");
			put(2465, "FRAGBAR");
			put(2467, "JETPACK_ICON");
			put(2468, "AIRTANK_ICON");
			put(2469, "STEROIDS_ICON");
			put(2470, "HOLODUKE_ICON");
			put(2471, "ACCESS_ICON");
			put(2472, "DIGITALNUM");
			put(2491, "DUKECAR");
			put(2482, "CAMCORNER");
			put(2484, "CAMLIGHT");
			put(2485, "LOGO");
			put(2486, "TITLE");
			put(2487, "NUKEWARNINGICON");
			put(2488, "MOUSECURSOR");
			put(2489, "SLIDEBAR");
			put(2492, "DREALMS");
			put(2493, "BETASCREEN");
			put(2494, "WINDOWBORDER1");
			put(2495, "TEXTBOX");
			put(2496, "WINDOWBORDER2");
			put(2497, "DUKENUKEM");
			put(2498, "THREEDEE");
			put(2499, "INGAMEDUKETHREEDEE");
			put(2500, "TENSCREEN");
			put(2501, "PLUTOPAKSPRITE");
			put(2510, "DEVISTATOR");
			put(2521, "KNEE");
			put(2523, "CROSSHAIR");
			put(2524, "FIRSTGUN");
			put(2528, "FIRSTGUNRELOAD");
			put(2530, "FALLINGCLIP");
			put(2531, "CLIPINHAND");
			put(2532, "HAND");
			put(2533, "SHELL");
			put(2535, "SHOTGUNSHELL");
			put(2536, "CHAINGUN");
			put(2544, "RPGGUN");
			put(2545, "RPGMUZZLEFLASH");
			put(2548, "FREEZE");
			put(2552, "CATLITE");
			put(2556, "SHRINKER");
			put(2563, "HANDHOLDINGLASER");
			put(2566, "TRIPBOMB");
			put(2567, "LASERLINE");
			put(2568, "HANDHOLDINGACCESS");
			put(2570, "HANDREMOTE");
			put(2573, "HANDTHROW");
			put(2576, "TIP");
			put(2578, "GLAIR");
			put(2581, "SCUBAMASK");
			put(2584, "SPACEMASK");
			put(2590, "FORCESPHERE");
			put(2595, "SHOTSPARK1");
			put(2605, "RPG");
			put(2612, "LASERSITE");
			put(2613, "SHOTGUN");
			put(2630, "BOSS1");
			put(2631, "BOSS1STAYPUT");
			put(2660, "BOSS1SHOOT");
			put(2670, "BOSS1LOB");
			put(2696, "BOSSTOP");
			put(2710, "BOSS2");
			put(2760, "BOSS3");
			put(2813, "SPINNINGNUKEICON");
			put(2820, "BIGFNTCURSOR");
			put(2821, "SMALLFNTCURSOR");
			put(2822, "STARTALPHANUM");
			put(2915, "ENDALPHANUM");
			put(2940, "BIGALPHANUM");
			put(3002, "BIGPERIOD");
			put(3003, "BIGCOMMA");
			put(3004, "BIGX");
			put(3005, "BIGQ");
			put(3006, "BIGSEMI");
			put(3007, "BIGCOLIN");
			put(3010, "THREEBYFIVE");
			put(3022, "BIGAPPOS");
			put(3026, "BLANK");
			put(3072, "MINIFONT");
			put(3164, "BUTTON1");
			put(3187, "GLASS3");
			put(3190, "RESPAWNMARKERRED");
			put(3200, "RESPAWNMARKERYELLOW");
			put(3210, "RESPAWNMARKERGREEN");
			put(3240, "BONUSSCREEN");
			put(3250, "VIEWBORDER");
			put(3260, "VICTORY1");
			put(3270, "ORDERING");
			put(3280, "TEXTSTORY");
			put(3281, "LOADSCREEN");
			put(3370, "BORNTOBEWILDSCREEN");
			put(3400, "BLIMP");
			put(3450, "FEM9");
			put(3701, "FOOTPRINT");
			put(4094, "POOP");
			put(4095, "FRAMEEFFECT1");
			put(4099, "PANNEL3");
			put(4120, "SCREENBREAK14");
			put(4123, "SCREENBREAK15");
			put(4125, "SCREENBREAK19");
			put(4127, "SCREENBREAK16");
			put(4128, "SCREENBREAK17");
			put(4129, "SCREENBREAK18");
			put(4130, "W_TECHWALL11");
			put(4131, "W_TECHWALL12");
			put(4132, "W_TECHWALL13");
			put(4133, "W_TECHWALL14");
			put(4134, "W_TECHWALL5");
			put(4136, "W_TECHWALL6");
			put(4138, "W_TECHWALL7");
			put(4140, "W_TECHWALL8");
			put(4142, "W_TECHWALL9");
			put(4100, "BPANNEL3");
			put(4144, "W_HITTECHWALL16");
			put(4145, "W_HITTECHWALL10");
			put(4147, "W_HITTECHWALL15");
			put(4181, "W_MILKSHELF");
			put(4203, "W_MILKSHELFBROKE");
			put(4240, "PURPLELAVA");
			put(4340, "LAVABUBBLE");
			put(4352, "DUKECUTOUT");
			put(4359, "TARGET");
			put(4360, "GUNPOWDERBARREL");
			put(4361, "DUCK");
			put(4367, "HATRACK");
			put(4370, "DESKLAMP");
			put(4372, "COFFEEMACHINE");
			put(4373, "CUPS");
			put(4374, "GAVALS");
			put(4375, "GAVALS2");
			put(4377, "POLICELIGHTPOLE");
			put(4388, "FLOORBASKET");
			put(4389, "PUKE");
			put(4391, "DOORTILE23");
			put(4396, "TOPSECRET");
			put(4397, "SPEAKER");
			put(4400, "TEDDYBEAR");
			put(4402, "ROBOTDOG");
			put(4404, "ROBOTPIRATE");
			put(4407, "ROBOTMOUSE");
			put(4410, "MAIL");
			put(4413, "MAILBAG");
			put(4427, "HOTMEAT");
			put(4438, "COFFEEMUG");
			put(4440, "DONUTS2");
			put(4444, "TRIPODCAMERA");
			put(4453, "METER");
			put(4454, "DESKPHONE");
			put(4458, "GUMBALLMACHINE");
			put(4459, "GUMBALLMACHINEBROKE");
			put(4460, "PAPER");
			put(4464, "MACE");
			put(4465, "GENERICPOLE2");
			put(4470, "XXXSTACY");
			put(4495, "WETFLOOR");
			put(4496, "BROOM");
			put(4497, "MOP");
			put(4502, "LETTER");
			put(4510, "PIRATE1A");
			put(4511, "PIRATE4A");
			put(4512, "PIRATE2A");
			put(4513, "PIRATE5A");
			put(4514, "PIRATE3A");
			put(4515, "PIRATE6A");
			put(4516, "PIRATEHALF");
			put(4520, "CHESTOFGOLD");
			put(4525, "SIDEBOLT1");
			put(4530, "FOODOBJECT1");
			put(4531, "FOODOBJECT2");
			put(4532, "FOODOBJECT3");
			put(4533, "FOODOBJECT4");
			put(4534, "FOODOBJECT5");
			put(4535, "FOODOBJECT6");
			put(4536, "FOODOBJECT7");
			put(4537, "FOODOBJECT8");
			put(4538, "FOODOBJECT9");
			put(4539, "FOODOBJECT10");
			put(4540, "FOODOBJECT11");
			put(4541, "FOODOBJECT12");
			put(4542, "FOODOBJECT13");
			put(4543, "FOODOBJECT14");
			put(4544, "FOODOBJECT15");
			put(4545, "FOODOBJECT16");
			put(4546, "FOODOBJECT17");
			put(4547, "FOODOBJECT18");
			put(4548, "FOODOBJECT19");
			put(4549, "FOODOBJECT20");
			put(4550, "HEADLAMP");
			put(4557, "TAMPON");
			put(4554, "SKINNEDCHICKEN");
			put(4555, "FEATHEREDCHICKEN");
			put(4560, "ROBOTDOG2");
			put(4569, "JOLLYMEAL");
			put(4570, "DUKEBURGER");
			put(4576, "SHOPPINGCART");
			put(4580, "CANWITHSOMETHING2");
			put(4581, "CANWITHSOMETHING3");
			put(4582, "CANWITHSOMETHING4");
			put(4590, "SNAKEP");
			put(4591, "DOLPHIN1");
			put(4592, "DOLPHIN2");
			put(4610, "NEWBEAST");
			put(4611, "NEWBEASTSTAYPUT");
			put(4690, "NEWBEASTJUMP");
			put(4670, "NEWBEASTHANG");
			put(4671, "NEWBEASTHANGDEAD");
			put(4740, "BOSS4");
			put(4741, "BOSS4STAYPUT");
			put(4864, "FEM10");
			put(4866, "TOUGHGAL");
			put(4871, "MAN");
			put(4872, "MAN2");
			put(4874, "WOMAN");
			put(4887, "PLEASEWAIT");
			put(4890, "NATURALLIGHTNING");
			put(4893, "WEATHERWARN");
			put(4900, "DUKETAG");
			put(4909, "SIGN1");
			put(4912, "SIGN2");
			put(4943, "JURYGUY");
		}
	};

	public static final TileNames DEFAULT_TILE_NAMES = new TileNames();

	public TileNames() {
		m_tileNames = DEFAULT_TILE_NAME_ENTRIES;
		m_tileNameChangeListeners = new Vector<TileNameChangeListener>();
	}
	
	protected TileNames(HashMap<Integer, String> tileNames) throws IllegalArgumentException {
		if(tileNames == null) {
			throw new IllegalArgumentException("Tile names hash map cannot be null!");
		}

		m_tileNames = new HashMap<Integer, String>(tileNames.size());
		m_tileNameChangeListeners = new Vector<TileNameChangeListener>();

		for(HashMap.Entry<Integer, String> tileNameEntry : tileNames.entrySet()) {
			setTileName(tileNameEntry.getKey(), tileNameEntry.getValue());
		}
	}

	public int numberOfTileNames() {
		return m_tileNames.size();
	}

	public boolean hasNameForTileNumber(int tileNumber) {
		return m_tileNames.containsKey(tileNumber);
	}

	public boolean hasTileName(String tileName) {
		return m_tileNames.containsValue(tileName);
	}

	public String getTileName(int tileNumber) {
		return m_tileNames.get(tileNumber);
	}

	public int getTileNumberByName(String tileName) {
		if(Utilities.isEmptyString(tileName)) {
			return -1;
		}

		String formattedTileName = tileName.trim();

		for(HashMap.Entry<Integer, String> tileNameEntry : m_tileNames.entrySet()) {
			if(tileNameEntry.getValue().equalsIgnoreCase(formattedTileName)) {
				return tileNameEntry.getKey();
			}
		}
		
		return -1;
	}
	
	public void setTileName(int tileNumber, String tileName) {
		if(tileNumber < 0) {
			throw new IllegalArgumentException("Tile number cannot be negative.");
		}

		if(Utilities.isEmptyString(tileName)) {
			throw new IllegalArgumentException("Tile names cannot be empty!");
		}

		String formattedTileName = tileName.trim();

		if(hasTileName(formattedTileName)) {
			throw new IllegalArgumentException("Tile name '" + formattedTileName + "' already exists!");
		}
		
		String previousTileName = m_tileNames.get(tileNumber);

		m_tileNames.put(tileNumber, formattedTileName);

		notifyTileNameChanged(tileNumber, previousTileName, tileName);
	}

	public boolean removeNameForTileNumber(int tileNumber) {
		if(tileNumber < 0) {
			return false;
		}
		
		String previousTileName = m_tileNames.remove(tileNumber);

		notifyTileNameChanged(tileNumber, previousTileName, null);

		return previousTileName != null;
	}

	public boolean removeTileName(String tileName) {
		if(Utilities.isEmptyString(tileName)) {
			return false;
		}

		String formattedTileName = tileName.trim();

		for(HashMap.Entry<Integer, String> tileNameEntry : m_tileNames.entrySet()) {
			if(tileNameEntry.getValue().equalsIgnoreCase(formattedTileName)) {
				int tileNumber = tileNameEntry.getKey();

				String previousTileName = m_tileNames.remove(tileNumber);

				notifyTileNameChanged(tileNumber, previousTileName, null);

				return previousTileName != null;
			}
		}
		
		return false;
	}

	public void clearAllTileNames() {
		m_tileNames.clear();

		notifyTileNamesChanged();
	}

	public int numberOfTileNameChangeListeners() {
		return m_tileNameChangeListeners.size();
	}
	
	public TileNameChangeListener getTileNameChangeListener(int index) {
		if(index < 0 || index >= m_tileNameChangeListeners.size()) { return null; }
		
		return m_tileNameChangeListeners.elementAt(index);
	}
	
	public boolean hasTileNameChangeListener(TileNameChangeListener c) {
		return m_tileNameChangeListeners.contains(c);
	}
	
	public int indexOfTileNameChangeListener(TileNameChangeListener c) {
		return m_tileNameChangeListeners.indexOf(c);
	}
	
	public boolean addTileNameChangeListener(TileNameChangeListener c) {
		if(c == null || m_tileNameChangeListeners.contains(c)) { return false; }
		
		m_tileNameChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeTileNameChangeListener(int index) {
		if(index < 0 || index >= m_tileNameChangeListeners.size()) { return false; }
		
		m_tileNameChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeTileNameChangeListener(TileNameChangeListener c) {
		if(c == null) { return false; }
		
		return m_tileNameChangeListeners.remove(c);
	}
	
	public void clearTileNameChangeListeners() {
		m_tileNameChangeListeners.clear();
	}
	
	public void notifyTileNamesChanged() {
		for(int i=0;i<m_tileNameChangeListeners.size();i++) {
			m_tileNameChangeListeners.elementAt(i).handleTileNamesChanged(this);
		}
	}
	
	public void notifyTileNameChanged(int tileNumber, String previousTileName, String newTileName) {
		for(int i=0;i<m_tileNameChangeListeners.size();i++) {
			m_tileNameChangeListeners.elementAt(i).handleTileNamesChanged(this);
			m_tileNameChangeListeners.elementAt(i).handleTileNameChanged(this, tileNumber, previousTileName, newTileName);
		}
	}

	public TileNames clone() {
		return clone(false);
	}

	public TileNames clone(boolean reassignTileChangeListeners) {
		TileNames newTileNames = new TileNames(m_tileNames);

		if(reassignTileChangeListeners) {
			newTileNames.m_tileNameChangeListeners = new Vector<TileNameChangeListener>(m_tileNameChangeListeners.size());
			
			for(int i = 0; i < m_tileNameChangeListeners.size(); i++) {
				newTileNames.m_tileNameChangeListeners.add(m_tileNameChangeListeners.elementAt(i));
			}
		}

		return newTileNames;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof TileNames)) {
			return false;
		}

		return m_tileNames.equals(((TileNames) o).m_tileNames);
	}

}
