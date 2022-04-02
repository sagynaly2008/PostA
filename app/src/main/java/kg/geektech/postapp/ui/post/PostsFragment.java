package kg.geektech.postapp.ui.post;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kg.geektech.postapp.App;
import kg.geektech.postapp.data.models.Post;
import kg.geektech.postapp.R;
import kg.geektech.postapp.databinding.FragmentPostsBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment implements OnClick{

    private FragmentPostsBinding binding;
    private PostAdapter adapter;
    private NavHostFragment navHostFragment;
    private NavController controller;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PostAdapter(this);
        navHostFragment= (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        controller = navHostFragment.getNavController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recycler.setAdapter(adapter);
        binding.fab.setOnClickListener(view1 -> {
            controller.navigate(R.id.formFragment);
        });
        binding.fab2.setOnClickListener(view1 -> {
            controller.navigate(R.id.action_postsFragment_to_peopleFragment);
        });

        App.api.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null){
                    adapter.setPosts(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(Post post) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", post);
        controller.navigate(R.id.action_postsFragment_to_formFragment, bundle);
    }

    @Override
    public AlertDialog onLongClick(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("вы действительно хотите удалить?");
        builder.setPositiveButton("Да", (dialogInterface, i) -> {
            App.api.deletePost(post.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    adapter.remove(post);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });
        builder.setNegativeButton("Нет", (dialogInterface, i) -> {
            controller.navigate(R.id.postsFragment);
        });
        builder.setCancelable(true);
        return builder.create();

    }
}