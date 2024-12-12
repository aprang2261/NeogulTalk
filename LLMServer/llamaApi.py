from flask import Flask, request, jsonify
from dotenv import load_dotenv
from openai import OpenAI

app = Flask(__name__)

load_dotenv()

client = OpenAI(base_url="http://localhost:1234/v1", api_key="lm-studio")

def local_llm_predict(prompt):
    try:
        completion = client.chat.completions.create(
            model="llama-3.2-korean-bllossom-3b",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.5,
            max_tokens=1024
        )
        return completion.choices[0].message.content
    except Exception as e:
        return f"Error in local LLM prediction: {str(e)}"

@app.route("/ask", methods=["POST"])
def ask_llm():
    data = request.json
    if "question" not in data:
        return jsonify({"error": "Question not provided"}), 400

    question = data["question"]

    My_prompt = f"""
    당신은 너굴톡이라는 채팅 프로그램의 도우미입니다. 다양한 사용자가 당신에게 질문할 수 있으며, 당신은 성심 성의껏 대답해 주어야 합니다.

    사용자 질문: {question}

    존댓말을 사용하여 답변하세요.

    답변:
    """
    response = local_llm_predict(My_prompt)
    return jsonify({"answer": response})


if __name__ == "__main__":
    app.run(port=5000, debug=True)
