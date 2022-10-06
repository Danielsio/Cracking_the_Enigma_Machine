package problem;

public enum Problem {
    // input rotors problems
    ROTOR_INPUT_NOT_ENOUGH_ELEMENTS, ROTOR_INPUT_TOO_MANY_ELEMENTS,
    ROTOR_INPUT_OUT_OF_RANGE_ID,
    ROTOR_DUPLICATION, ROTOR_VALIDATE_EMPTY_STRING,
    ROTOR_VALIDATE_NO_SEPERATOR, ROTOR_INPUT_HAS_SPACE, ROTOR_INPUT_NUMBER_FORMAT_EXCEPTION,

    // input windows chars problems
    WINDOW_INPUT_TOO_MANY_LETTERS, WINDOW_INPUT_TOO_FEW_LETTERS,
    WINDOWS_INPUT_NOT_IN_ALPHABET,

    // input reflector problems
    REFLECTOR_INPUT_OUT_OF_RANGE_ID, REFLECTOR_INPUT_EMPTY_STRING, REFLECTOR_INPUT_NOT_A_NUMBER,

    // input plugs problems
    SELF_PLUGGING, ALREADY_PLUGGED, PLUGS_INPUT_NOT_IN_ALPHABET, PLUGS_INPUT_ODD_ALPHABET_AMOUNT, PLUGS_MISSING_VALUES,

    // config problems
    NO_CONFIGURATION,

    // XML file problems
    FILE_NOT_FOUND, JAXB_ERROR,
    FILE_NOT_IN_FORMAT,

    // xml file Alphabet problems
    FILE_ODD_ALPHABET_AMOUNT,

    //  xml file rotors problems
    FILE_NOT_ENOUGH_ROTORS, FILE_ROTORS_COUNT_BELOW_TWO,
    FILE_ROTOR_MAPPING_NOT_IN_ALPHABET, FILE_ROTOR_MAPPING_NOT_A_SINGLE_LETTER,
    FILE_ROTOR_INVALID_ID_RANGE, FILE_OUT_OF_RANGE_NOTCH, FILE_ROTOR_COUNT_HIGHER_THAN_99,
    FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH, FILE_ROTOR_MAPPING_DUPPLICATION,

    //  xml file reflectors problems
    FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC, FILE_REFLECTOR_INVALID_ID_RANGE, FILE_REFLECTOR_SELF_MAPPING,
    FILE_REFLECTOR_ID_DUPLICATIONS, FILE_REFLECTOR_MAPPING_NOT_IN_ALPHABET,
    FILE_TOO_MANY_REFLECTORS, FILE_REFLECTOR_OUT_OF_RANGE_ID, FILE_REFLECTOR_MAPPING_DUPPLICATION,

    // input cipher text problems
    CIPHER_INPUT_NOT_IN_ALPHABET,

    FILE_EXISTING_LOAD_FAILED,

    UNKNOWN, NO_PROBLEM,
    NO_REFLECTOR_BEEN_CHOSEN, NO_LOADED_MACHINE,
    CIPHER_INPUT_EMPTY_STRING, FILE_TOO_MANY_AGENTS, FILE_TOO_LITTLE_AGENTS,
    INVALID_DIFFICULTY_LEVEL, BATTLEFIELD_NAME_ALREADY_EXIST, ALLIES_COUNT_LESS_THAN_1,
    NOT_ALL_WORDS_IN_DICTIONARY, UNAUTHORIZED_CLIENT_ACCESS, NO_UBOAT_NAME, MISSING_QUERY_USERNAME,
    USERNAME_ALREADY_EXIST, UBOAT_NAME_DOESNT_EXIST, MISSING_QUERY_PARAMETER, MORE_THAN_ONE_FILE,
    NOT_FOUND_UBOAT_NAME, BATTLEFIELD_NOT_CONFIGURED, ALLIE_NAME_NOT_FOUND, BATTLEFIELD_ALREADY_CONFIGURED, NO_ALLIE_NAME, AGENT_NAME_DOESNT_EXIST;


}
