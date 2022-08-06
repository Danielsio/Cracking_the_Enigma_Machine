import machine.component.Reflector;
import machine.component.Rotor;
import dto.*;
import machine.EnigmaMachine;
import machine.jaxb.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import static machine.EnigmaMachine.advanceCipherCounter;
import static machine.EnigmaMachine.getCipherCounter;
import static utill.Utility.*;


public class EnigmaEngine implements Engine {

    // The engine contains the Enigma Machine instance.
    private EnigmaMachine machine;

    // a structure that holds the configuration parameters that are valid,
    // and still not in the machine.
    private Secret pendingConfiguration;

    // engine constructor
    public EnigmaEngine(){
        this.pendingConfiguration = new Secret();
    }

    // creating new machine instance using all the parts the machine needs.
    public void buildMachine(ArrayList<Rotor> availableRotors, ArrayList<Reflector> availableReflectors, int rotorsCount, String alphabet, Map<Character, Integer> character2index){
        machine = new EnigmaMachine(availableRotors, availableReflectors, rotorsCount, alphabet, character2index);
    }

    // updating the current machine configurations.
    // based on String of input from the user.
    public void updateConfiguration(String rotors, String windows, String reflector ,String plugs){

        // build reflectorID
        int reflectorID = romanToDecimal(reflector);

        // build rotorsIDs list
        ArrayList<Integer> rotorsIDs = new ArrayList<>();
        String[] rotorsIDsAsStrings = rotors.split(",");

        for (int i = rotorsIDsAsStrings.length - 1; i >= 0; i--) {
            rotorsIDs.add(Integer.parseInt(rotorsIDsAsStrings[i]));
        }

        // build windowOffsets
        ArrayList<Integer> windowOfssets = new ArrayList<>();

        for (int i = windows.length() - 1; i >= 0; i--) {
            int j = windows.length() - 1 - i ;
            Rotor r = machine.getRotorByID( rotorsIDs.get(j));
            int offset = r.translateChar2Offset(windows.charAt(i));
            windowOfssets.add(offset);
        }


        machine.setMachineConfiguration(rotorsIDs, windowOfssets, reflectorID, plugs);
    }

    // ciphering text with the cipher method of "machine".
    public String cipherText ( String toCipher ) {
        StringBuilder cipheredText = new StringBuilder();

        for (Character currentChar : toCipher.toCharArray()) {
            cipheredText.append(machine.cipher(currentChar));
        }

        advanceCipherCounter();
        return cipheredText.toString();
    }

    // get fileName from user and load Xml file to build new machine.
    public void buildMachineFromXmlFile(String fileName) {
        try {
            InputStream inputStream = new FileInputStream(new File(fileName));
            CTEEnigma cteEnigma = deserializeFrom(inputStream);

            System.out.println(cteEnigma);
            buildMachineFromCTEEnigma(cteEnigma);
            System.out.println(machine);


        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // unmarshalling the schema of provided Xml file to create jaxb classes
    // to help build the machine from a xml file
    private static CTEEnigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }

    public static String JAXB_XML_PACKAGE_NAME = "machine.jaxb.generated";

    // convert all data from jaxb classes to our own classes.
    private void buildMachineFromCTEEnigma( CTEEnigma cteEnigma ){
        CTEMachine cteMachine = cteEnigma.getCTEMachine();

        ArrayList<Rotor> availableRotors = new ArrayList<>();
        ArrayList<Reflector> availableReflectors = new ArrayList<>();
        int rotorsCount = cteMachine.getRotorsCount();

        // initialize alphabet and character2index
        String alphabet = cteMachine.getABC().trim();
        Map<Character, Integer> character2index= new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            character2index.put(alphabet.charAt(i), i);
        }

        // initialize rotors
        for ( CTERotor cteRotor : cteMachine.getCTERotors().getCTERotor() ) {

            int currentID = cteRotor.getId();
            int currrentNotchIndex = cteRotor.getNotch() - 1;

            ArrayList<Integer> mapRotorForward = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));
            ArrayList<Integer> mapRotorBackward = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));
            Map<Character, Integer> forwardTranslatorChar2index = new HashMap<>();
            Map<Character, Integer> backwardTranslatorChar2index = new HashMap<>();
            int index = 0;

            // go through all right positions to build the translators Char to index
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()){
                forwardTranslatorChar2index.put(ctePosition.getRight().charAt(0), index);
                backwardTranslatorChar2index.put(ctePosition.getLeft().charAt(0), index);
                index++;
            }

            int indexOfLeftSide = 0;

            // go through all left positions to build the forwarding map
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()){
               int indexOfRightSide = forwardTranslatorChar2index.get(ctePosition.getLeft().charAt(0));
               mapRotorForward.set(indexOfRightSide, indexOfLeftSide);
               indexOfLeftSide++;
            }

            indexOfLeftSide = 0;

            // go through all right positions to build the backwarding map
            for (CTEPositioning ctePosition : cteRotor.getCTEPositioning()){
                int indexOfRightSide = backwardTranslatorChar2index.get(ctePosition.getRight().charAt(0));
                mapRotorBackward.set(indexOfRightSide, indexOfLeftSide);
                indexOfLeftSide++;
            }

            // creating a new rotor
            Rotor currentRotor = new Rotor(currentID, currrentNotchIndex,
                    forwardTranslatorChar2index, alphabet.length(),
                    mapRotorForward, mapRotorBackward);

            availableRotors.add(currentRotor);
        }

        //initialize reflectors
        for ( CTEReflector cteReflector : cteMachine.getCTEReflectors().getCTEReflector() ){

            int currentID = romanToDecimal( cteReflector.getId() );

            ArrayList<Integer> reflectorMapping = new ArrayList<>(Collections.nCopies(alphabet.length(), 0));

            // creating the mapping of the reflector
            for (CTEReflect cteReflect : cteReflector.getCTEReflect()){
                int input = cteReflect.getInput() -1;
                int output = cteReflect.getOutput() -1;
                reflectorMapping.set(input, output);
                reflectorMapping.set(output, input);
            }

            // creating a new reflector
            Reflector currentReflector = new Reflector(currentID, reflectorMapping);

            availableReflectors.add(currentReflector);
        }

        /** checkValidation */

        // sort rotors and reflector by id
        Comparator<Rotor> rotorComparator = (r1, r2) -> r1.getId() - r2.getId();
        availableRotors.sort(rotorComparator);
        Comparator<Reflector> reflectorComparator = (r1, r2) -> r1.getId() - r2.getId();
        availableReflectors.sort(reflectorComparator);

        buildMachine(availableRotors, availableReflectors, rotorsCount, alphabet, character2index);


    }


    /**
     * fetching the current machine specifications.
     * @return DTO object that represents the specs.
     */
    public DTOspecs displayMachineSpecifications () {

        int availableRotorsCount = machine.getAvailableRotorsLen();
        int inUseRotorsCount = machine.getRotorsCount();
        List<Integer> notchPositions = machine.getAllNotchPositions();
        int availableReflectorsCount = machine.getAvailableReflectorsLen();
        int cipheredTextsCount = getCipherCounter();

        List<Integer> inUseRotorsIDs = machine.getInUseRotorsIDs();
        List<Character> windowsCharacters = machine.getAllWindowsCharacters();
        String inUseReflectorSymbol = decimalToRoman(machine.getInUseReflector().getId());
        List<CharacterPair> inUsePlugs = machine.getListOfPlugPairs();


        return new DTOspecs(availableRotorsCount, inUseRotorsCount, notchPositions,
                availableReflectorsCount, cipheredTextsCount, inUseRotorsIDs, windowsCharacters,
                inUseReflectorSymbol, inUsePlugs);
    }

    public DTOconfig selectConfigurationManual (String rotors, String windows, String reflector ,String plugs){

        boolean isSucceed, isRotorsOK, isWindowsOK, isReflectorOK, isPlugsOK;
        isSucceed = isRotorsOK = isWindowsOK = isReflectorOK = isPlugsOK = false;

        String rotorsProblem = "";
        String windowsProblem = "";
        String reflectorProblem = "";
        String plugsProblem = "";

        // check rotors validation
        //isRotorsOK = checkValidRotors(rotors);
        if (isRotorsOK) {
            pendingConfiguration.setRotors(rotors);
        }

        // check windows validation
        //isWidowsOK = checkValidWindows(windows);
        if (isWindowsOK) {
            pendingConfiguration.setWindows(windows);
        }

        // check reflector validation
        //isReflectorOK = checkValidReflector(reflector);
        if (isReflectorOK) {
            pendingConfiguration.setReflector(reflector);
        }

        // check plugs validation
        //isPlugsOK = checkValidPlugs(plugs);
        if (isPlugsOK) {
            pendingConfiguration.setPlugs(plugs);
        }

        // build secret

        if (isRotorsOK && isWindowsOK && isReflectorOK && isPlugsOK){
            isSucceed = true;
            updateConfiguration(rotors, windows, reflector, plugs);
        }

        return new DTOconfig(isSucceed, isRotorsOK, isWindowsOK, isReflectorOK, isPlugsOK,
                rotorsProblem, windowsProblem, reflectorProblem, plugsProblem);
    }

    /*
    public DTOsecretConfig selectConfigurationAuto (){

        List<Integer> randomGeneratedRotorIDs= new ArrayList<>();
        int randomGeneratedReflectorID;
        List<Integer> randomGeneratedWindowOffsets = new ArrayList<>();

        // randomizes rotors ID and order
        for (int i = 0; i < machine.getRotorsCount(); i++) {
            int randomRotorID = (int)Math.ceil( Math.random() * machine.getAvailableRotorsLen() );
            while( randomGeneratedRotorIDs.contains(randomRotorID)){
                randomRotorID = (int)Math.ceil( Math.random() * machine.getAvailableRotorsLen() );
            }

            randomGeneratedRotorIDs.add(randomRotorID);
        }

        // randomizes reflector ID
        randomGeneratedReflectorID = (int)Math.ceil( Math.random() * machine.getAvailableReflectorsLen() );

        // randomizes window offsets
        for (int i = 0; i < machine.getRotorsCount(); i++) {
            randomGeneratedWindowOffsets.add( (int)Math.floor( Math.random() * machine.getAlphabet().length()) );
        }

        // randomizes plugs
        int randomPlugsCount =  (int)Math.floor(Math.random() * (machine.getAlphabet().length() + 1)) / 2;

        for (int i = 0; i < randomPlugsCount; i++) {
            int leftSizeOfPlug = (int)Math.floor( Math.random() * machine.getAlphabet().length());
            int rightSizeOfPlug = (int)Math.floor( Math.random() * machine.getAlphabet().length());

            /* check validation of plugs. if they already in the plugs "array"  */
       /* }



        // updates the configuration
        machine.setMachineConfiguration(randomGeneratedRotorIDs, randomGeneratedWindowOffsets, randomGeneratedReflectorID, );

        List<Character> windowCharacters = machine.getAllWindowsCharacters();

        return new DTOsecretConfig( rotor, windows, reflector, plugs);
    }*/

    public DTOciphertext cipherInputText (String inputText) {

        boolean isSucceed = true;
        String outputText;
        String problem = "";

        // check valid ABC
        //problem = isAllCharsInAlphabet(inputText);

        if (problem.equals("")){
            outputText = cipherText(inputText);
        }else {
            outputText = problem;
        }

        return new DTOciphertext(isSucceed , outputText);
    }

    public DTO resetConfiguration () {

        boolean isSucceed = true;
        String detail = "";

        for (int i = 0; i < machine.getRotorsCount(); i++) {

            int currentOffset = machine.getInUseWindowsOffsets().get(i);
            machine.getInUseRotors().get(i).rotateToOffset(currentOffset);
        }

        return new DTO(isSucceed, detail);
    }


    public void buildAlphabetMap(){

    }


    /*

    1.
    learn JAXB
    build machine from XML

    2.
    implement current config

    3 + 4 .
    decide on the fields of the config and where to put them

    5.
    test cipher methods
    implement "cipherText" in "Engine"
    limit - can't operate before 3 or 4
    check valid input

    6.

    7.

    8.
    write function 'exit'



     */

    @Override
    public String toString() {
        return "EnigmaEngine{" +
                "machine=" + machine +
                '}';
    }
}
