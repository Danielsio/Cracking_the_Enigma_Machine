package servlets.uboat;

import com.google.gson.Gson;
import constants.Constants;
import dto.DTOciphertext;
import dto.DTOstatus;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import problem.Problem;
import utils.SessionUtils;

import java.io.IOException;

import static utils.ServletUtils.validateAuthorization;

public class CipherTextServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get the text from the cody of post request 
        String textToCipher = req.getParameter("Text");

        if (textToCipher != null) {
            textToCipher = textToCipher.toUpperCase();
        } else {
            // error no Text parameter
        }

        // get userName of uboat 
        String userNameFromSession = SessionUtils.getUsername(req);
        Gson gson = new Gson();
        resp.setContentType("application/json");
        boolean isValid = validateAuthorization(userNameFromSession, resp, gson);
        if (isValid) {
            // get engine from context
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            // validate the text to cipher
            if (engine.isAllWordsInDictionary(textToCipher, userNameFromSession)) {
                DTOciphertext cipherStatus = engine.cipherInputText(textToCipher, userNameFromSession);
                if (!cipherStatus.isSucceed()) {
                    // can happen? when putting words manual which has chars not in alphabet
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                resp.getWriter().println(gson.toJson(cipherStatus));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(gson.toJson(new DTOstatus(false, Problem.NOT_ALL_WORDS_IN_DICTIONARY)));
            }
        }
    }

}