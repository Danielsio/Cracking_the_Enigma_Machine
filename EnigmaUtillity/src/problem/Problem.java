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
    REFLECTOR_INPUT_OUT_OF_RANGE_ID,

    // input plugs problems
    SELF_PLUGGING, ALREADY_PLUGGED, PLUGS_INPUT_NOT_IN_ALPHABET, PLUGS_INPUT_ODD_ALPHABET_AMOUNT,

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
    FILE_ROTOR_MAPPING_NOT_EQUAL_TO_ALPHABET_LENGTH,

    //  xml file reflectors problems
    FILE_NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC , FILE_REFLECTOR_INVALID_ID_RANGE, FILE_REFLECTOR_SELF_MAPPING,
    FILE_REFLECTOR_ID_DUPLICATIONS,
    FILE_TOO_MANY_REFLECTORS, FILE_REFLECTOR_OUT_OF_RANGE_ID,

    // input cipher text problems
    CIPHER_INPUT_NOT_IN_ALPHABET,

    FILE_EXISTING_LOAD_FAILED,

    UNKNOWN, NO_PROBLEM;

    public void printMe() {
        System.out.println(this.name());
    }

}